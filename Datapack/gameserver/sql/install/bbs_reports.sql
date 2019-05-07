DROP TABLE IF EXISTS `bbs_reports`;
CREATE TABLE IF NOT EXISTS `bbs_reports`
(
    `report_id`  INT NOT NULL AUTO_INCREMENT,
    `player_id`   INT UNSIGNED NOT NULL,
    `report`     VARCHAR(500)  NOT NULL,
    `report_date` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `pending` BOOL NOT NULL DEFAULT TRUE,
    PRIMARY KEY (`report_id`),
    KEY (`report_date`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
