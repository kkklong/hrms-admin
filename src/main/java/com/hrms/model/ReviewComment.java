package com.hrms.model;

import lombok.Data;

@Data
public class ReviewComment {
    private String by;
    private String reason;

    public ReviewComment(String by, String reason) {
        this.by = by;
        this.reason = reason;
    }
}
