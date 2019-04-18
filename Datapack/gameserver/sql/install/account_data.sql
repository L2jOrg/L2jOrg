DROP TABLE IF EXISTS `account_data`;

CREATE TABLE IF NOT EXISTS `account_data`
(
    `account` VARCHAR(45)  NOT NULL DEFAULT '',
    `coin`  INT NOT NULL DEFAULT 0,
    `vip_point` BIGINT NOT NULL DEFAULT 0,
    `vip_tier_expiration` BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`account`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
