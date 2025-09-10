package com.hrms.model;

import lombok.Data;

@Data
public class HistoryReview {
    private String action;
    private Integer id; //操作人ID
    private String by; //操作人名字
    private String time;

    public HistoryReview(String action, Integer id, String by, String time) {
        this.action = action;
        this.id = id;
        this.by = by;
        this.time = time;
    }
}
