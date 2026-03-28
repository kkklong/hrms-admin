package com.hrms.integration.telegram;

import com.hrms.config.properties.TelegramBotProperties;
import com.hrms.entity.Employee;
import com.hrms.service.EmployeeService;
import com.hrms.service.RemoteAuditRecordService;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ForceReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

@Slf4j
public class HrmsTelegramBot extends TelegramLongPollingBot {

    private final TelegramBotProperties properties;
    private final EmployeeService employeeService;
    @Resource
    private RemoteAuditRecordService remoteAuditRecordService;

    // 定義提示語常數
    private static final String PROMPT_INPUT_ACCOUNT = "請用回覆輸入您的 HRMS 帳號：";
    private static final String CALLBACK_AUDIT_CONFIRM = "AUDIT_CONFIRM:";

    public HrmsTelegramBot(TelegramBotProperties properties, EmployeeService employeeService) {
        super(properties.getToken());
        this.properties = properties;
        this.employeeService = employeeService;
        log.info("botUsername:{} botToken:{} allowGroups:{}", properties.getUsername(), properties.getToken(), properties.getAllowGroups());
    }

    @Override
    public void onUpdateReceived(Update update) {
        log.debug("收到更新 update:{}", update);

        // 1. 處理 Inline Button 回調
        if (update.hasCallbackQuery()) {
            handleCallback(update.getCallbackQuery());
            return;
        }

        // 2. 處理文字訊息
        if (update.hasMessage()) {
            TelegramCommand command = TelegramCommand.fromText(update.getMessage().getText());

            // 優先指令：/getGroupId
            if (TelegramCommand.GET_GROUP_ID == command) {
                sendText(update.getMessage().getChatId(), "群組 ID: " + update.getMessage().getChatId());
                return;
            }

            String type = update.getMessage().getChat().getType();
            switch (type) {
                case "group":
                case "supergroup":
                    handleGroupMessage(update);
                    break;
                case "private":
                    handlePrivateMessage(update, command);
                    break;
                default:
                    log.debug("未處理的聊天類型: {}", type);
            }
        }
    }

    private void handleCallback(CallbackQuery callbackQuery) {
        String data = callbackQuery.getData();
        if (data.startsWith(CALLBACK_AUDIT_CONFIRM)) {
            String uuid = data.substring(CALLBACK_AUDIT_CONFIRM.length());
            Long userId = callbackQuery.getFrom().getId();

            // TODO: 呼叫 Service 更新稽核紀錄狀態 (recordId, userId)

            log.info("收到遠端稽核確認: UUID={}, UserID={}", uuid, userId);
            // 判斷打卡結果並設定回應訊息
            boolean isSuccess = remoteAuditRecordService.updateRemoteRecordStatus(uuid);
            String callbackMessage = isSuccess ? "打卡成功！" : "打卡異常！";

            // 回應 Callback (停止 loading 動畫)
            AnswerCallbackQuery answer = AnswerCallbackQuery.builder()
                    .callbackQueryId(callbackQuery.getId())
                    .text(callbackMessage)
                    .build();

            // 編輯原訊息，移除按鈕並顯示確認狀態
            String originalText = "";
            if (callbackQuery.getMessage() instanceof Message message) {
                originalText = message.getText();
            }

            // 根據打卡結果設定不同的編輯訊息
            String statusText = isSuccess
                    ? "\n\n✅ 已於 " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) + " 確認在席。"
                    : "\n\n❌ 打卡超時於 " + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) ;

            EditMessageText edit = EditMessageText.builder()
                    .chatId(callbackQuery.getMessage().getChatId().toString())
                    .messageId(callbackQuery.getMessage().getMessageId())
                    .text(originalText + statusText)
                    .build();

            try {
                execute(answer);
                execute(edit);
            } catch (TelegramApiException e) {
                log.error("處理 Callback 失敗", e);
            }
        }
    }

    private void handleGroupMessage(Update update) {
        Long chatId = update.getMessage().getChatId();
        if (properties.getAllowGroups() == null || !properties.getAllowGroups().contains(chatId)) {
            log.warn("群組 {} 未被允許使用", chatId);
            return;
        }
        // 群組訊息處理邏輯
        if (update.getMessage().hasText() && properties.getUsername() != null) {
            String text = update.getMessage().getText();
            if (text.contains("@" + properties.getUsername())) {
                String message = String.format("目前本機器人尚未開放群組功能。\n若需使用系統功能（如：連結 HRMS），請點擊下方連結私訊機器人：\nhttps://t.me/%s\n或使用@%s搜尋加入"
                        , properties.getUsername(), properties.getUsername());
                sendText(chatId, message);
            }
        }
    }

    private void handlePrivateMessage(Update update, TelegramCommand command) {
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();
        String text = update.getMessage().getText();

        // 1. 處理指令
        if (command != null) {
            switch (command) {
                case START:
                    sendText(chatId, "歡迎使用HRM機器人！請使用 /link_hrm 指令來綁定您的帳號。");
                    break;
                case LINK_HRM:
                    handleLinkHrm(update);
                    break;

            }
            return;
        }

        // 2. 處理 ForceReply 回覆 (目前僅用於綁定帳號)
        Message replyToMessage = update.getMessage().getReplyToMessage();
        if (replyToMessage != null && replyToMessage.getText() != null) {
            if (properties.getUsername().equals(replyToMessage.getFrom().getUserName())) {
                if (replyToMessage.getText().endsWith(PROMPT_INPUT_ACCOUNT)) {
                    processLinkHrm(chatId, userId, text);
                    return;
                }
            }
        }

        log.debug("私人訊息：{}", text);
    }

    private void handleLinkHrm(Update update) {
        Long chatId = update.getMessage().getChatId();
        Long userId = update.getMessage().getFrom().getId();
        String text = update.getMessage().getText();
        String[] parts = text.split("\\s+");

        if (parts.length >= 2) {
            processLinkHrm(chatId, userId, parts[1]);
        } else {
            sendForceReply(chatId, PROMPT_INPUT_ACCOUNT);
        }
    }

    private void processLinkHrm(Long chatId, Long userId, String account) {
        Employee targetEmployee = employeeService.getByAccount(account);

        if (targetEmployee == null) {
            sendForceReply(chatId, "帳號不存在。\n" + PROMPT_INPUT_ACCOUNT);
            return;
        }

        String telegramId = String.valueOf(userId);

        Employee existingUserOfTg = employeeService.lambdaQuery().eq(Employee::getTelegram, telegramId).one();
        if (existingUserOfTg != null) {
            if (existingUserOfTg.getId().equals(targetEmployee.getId())) {
                sendText(chatId, "您的帳號已綁定此 Telegram ID。");
            } else {
                sendText(chatId, String.format("此 Telegram ID 已被其他帳號 %s(%s) 使用。請聯繫管理員。",
                        existingUserOfTg.getAccount(), existingUserOfTg.getFullName()));
            }
            return;
        }

        if (targetEmployee.getTelegram() != null && !targetEmployee.getTelegram().isEmpty()) {
            sendText(chatId, "此帳號已綁定其他 Telegram ID，請登入系統修改個人telegram設定。");
            return;
        }

        targetEmployee.setTelegram(telegramId);
        employeeService.updateById(targetEmployee);
        sendText(chatId, "綁定成功！");
    }

    /**
     * 發送遠端稽核點名訊息 (供外部調用)
     * 使用 Inline Button 進行確認
     *
     * @param chatId Telegram Chat ID
     * @param timeoutMinutes 回應時限(分鐘)
     * @param token 稽核紀錄ID (放入 Callback Data)
     * @return 發送的訊息物件
     */
    public Message sendRemoteAudit(Long chatId, LocalDateTime sentAt, Integer timeoutMinutes, String token) {
        String nowStr = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").format(sentAt);
        int timeout = (timeoutMinutes != null) ? timeoutMinutes : 10;
        String text = String.format("[%s]遠端稽核點名：請於 %d 分鐘內點擊下方按鈕確認在席。", nowStr ,timeout);

        InlineKeyboardButton button = InlineKeyboardButton.builder()
                .text("確認在席")
                .callbackData(CALLBACK_AUDIT_CONFIRM + token)
                .build();

        InlineKeyboardMarkup markup = InlineKeyboardMarkup.builder()
                .keyboardRow(Collections.singletonList(button))
                .build();

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .replyMarkup(markup)
                .build();

        try {
            log.info("發送稽核: 於 {} 發送遠端稽核點名訊息至 {} (時限 {} 分鐘)", nowStr, chatId, timeout);
            return execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("發送稽核訊息失敗", e);
            return null;
        }
    }

    public void sendAlert(Long chatId, String employeeNickName, String alertMessage) {
        String text = String.format("遠端稽核警示：員工[%s] %s", employeeNickName, alertMessage);

        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .build();

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("發送示警訊息失敗", e);
        }
    }



    private void sendForceReply(Long chatId, String text) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text)
                .replyMarkup(new ForceReplyKeyboard(true))
                .build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("發送強制回覆失敗", e);
        }
    }

    private void sendText(Long chatId, String text) {
        SendMessage sendMessage = SendMessage.builder()
                .chatId(chatId.toString())
                .text(text).build();
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.error("發送訊息失敗", e);
        }
    }

    @Override
    public String getBotUsername() {
        return properties.getUsername();
    }

}