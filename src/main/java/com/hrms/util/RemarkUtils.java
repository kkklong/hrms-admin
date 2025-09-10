package com.hrms.util;

import com.hrms.enums.ErrorCode;
import com.hrms.exception.ServiceException;
import com.hrms.model.ReviewComment;
import com.hrms.model.UserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class RemarkUtils {
    /**
     * 添加審核意見紀錄
     */
    public static void appendReviewComment(
            Supplier<String> getRemark,
            Consumer<String> setRemark,
            String mark,
            UserInfo userInfo
    ) {
        try {
            // 獲取現有的審核意見 JSON 字符串
            String reviewJson = getRemark.get();
            List<ReviewComment> reviewList;

            if (reviewJson == null || reviewJson.isEmpty()) {
                reviewList = new ArrayList<>();
            } else {
                reviewList = JsonUtils.toList(reviewJson, ReviewComment.class);
            }

            // 創建新的審核意見
            ReviewComment newComment = new ReviewComment(userInfo.getFullName(), mark);

            // 將新的審核意見添加到審核意見列表中
            reviewList.add(newComment);

            // 將更新後的審核意見列表轉換回 JSON 字符串
            String updatedReviewJson = JsonUtils.toJSON(reviewList);

            // 設置更新後的審核意見 JSON 字符串
            setRemark.accept(updatedReviewJson);

        } catch (Exception e) {
            throw new ServiceException(ErrorCode.COMMENT_UPDATE_FAIL);
        }
    }
}
