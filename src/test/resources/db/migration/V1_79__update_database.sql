-- 更新 submit_notification 表的注释
ALTER TABLE submit_notification
    MODIFY COLUMN status TINYINT DEFAULT NULL COMMENT '0:未發布、1:已發布、2:已撤銷';

