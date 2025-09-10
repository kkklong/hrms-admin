package com.hrms.util;

import com.hrms.enums.ErrorCode;
import com.hrms.exception.ServiceException;
import com.hrms.model.HistoryReview;
import com.hrms.model.UserInfo;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class HistoryUtils {
    /**
     * 添加歷史紀錄
     */
    public static void appendHistoryReview(
            Supplier<String> getHistoryReview,
            Consumer<String> setHistoryReview,
            String action,
            UserInfo userInfo
    ) {
        try {
            // 獲取當前歷史記錄的 JSON 字符串
            String historyJson = getHistoryReview.get();

            // 如果歷史記錄 JSON 為空，則創建一個新的列表
            List<HistoryReview> historyList;
            if (historyJson == null || historyJson.isEmpty()) {
                historyList = new ArrayList<>();
            } else {
                historyList = JsonUtils.toList(historyJson, HistoryReview.class);
            }

            // 創建新的事件節點
            HistoryReview newEvent = new HistoryReview(action, userInfo.getId(), userInfo.getFullName(), LocalDateTime.now().toString());

            // 將新的事件添加到歷史記錄列表中
            historyList.add(newEvent);

            // 將更新后的歷史記錄列錶轉換回 JSON 字符串
            String updatedHistoryJson = JsonUtils.toJSON(historyList);

            // 設置更新后的歷史記錄 JSON 字符串
            setHistoryReview.accept(updatedHistoryJson);

        } catch (Exception e) {
            throw new ServiceException(ErrorCode.HISTORY_REVIEW_UPDATE_FAIL);
        }
    }
}
