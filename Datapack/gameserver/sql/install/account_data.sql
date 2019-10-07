DROP TABLE IF EXISTS `account_data`;

CREATE TABLE IF NOT EXISTS `account_data`
(
    `account` VARCHAR(45)  NOT NULL DEFAULT '',
    `sec_auth_password` VARCHAR(255),
    `sec_auth_attempts` TINYINT DEFAULT 0,
    `coin` INT NOT NULL DEFAULT 0,
    `vip_point` BIGINT NOT NULL DEFAULT 0,
    `vip_tier_expiration` BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`account`)
) ENGINE = InnoDB DEFAULT CHARSET=UTF8MB4;
