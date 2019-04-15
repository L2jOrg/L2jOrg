DROP TABLE IF EXISTS `account_gsdata`;

CREATE TABLE IF NOT EXISTS `account_data`
(
    `account` VARCHAR(45)  NOT NULL DEFAULT '',
    `vip_point` BIGINT NOT NULL DEFAULT 0,
    `vip_tier_expiration` BIGINT NOT NULL DEFAULT 0,
    `silver_coin` BIGINT NOT NULL DEFAULT 0,
    `rusty_coin` BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`account`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8;
