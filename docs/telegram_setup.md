# Telegram Bot 配置

## 前置作業
1. 使用 [@BotFather](https://t.me/BotFather) 建立一個機器人。
2. 取得 **Bot Token**。

## 配置
在 `application.yml` 中配置以下屬性，或使用環境變數：

```yaml
telegram:
  bot:
    username: <Your_Bot_Username>
    token: <Your_Bot_Token>
    allow-groups:
      - <Allowed_Group_ID_1>
      - <Allowed_Group_ID_2>
```

## 設定指令
建立機器人後，與 [@BotFather](https://t.me/BotFather) 對話並使用 `/setcommands` 指令。當 BotFather 要求提供指令列表時，請複製並發送以下內容：

```text
get_group_id - 取得當前群組 ID
link_hrm - 連結 HRM 帳號
join - 加入群組
```

## 使用方法
1. 將機器人加入您的群組。
2. 在Telegram確認機器人有讀取訊息權限，若沒有請把機器人加入管理員。
3. 執行 `/getGroupId` 以取得群組 ID。
4. 將群組 ID 新增至 `telegram.bot.allow-groups` 配置中。