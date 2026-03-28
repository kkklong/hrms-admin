package com.hrms.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RemoteAuditStatus {

    SEND((byte)1,"已發送"),
    RESPONDED((byte)2,"已回覆"),
    TIMEOUT((byte)3, "超時"),
    CANCELED((byte)4, "取消")
    ;
    private byte value;
    private  String description;
    public static RemoteAuditStatus getRemoteAuditStatusByValue(byte value) {
        for (RemoteAuditStatus remoteAuditStatus : RemoteAuditStatus.values()) {
            if (remoteAuditStatus.getValue() == value) {
                return remoteAuditStatus;
            }
        }
        return null;
    }
    public static String getDescriptionByValue(byte value) {
        for (RemoteAuditStatus remoteAuditStatus : RemoteAuditStatus.values()) {
            if (remoteAuditStatus.getValue() == value) {
                return remoteAuditStatus.getDescription();
            }
        }
        return null;
    }
}
