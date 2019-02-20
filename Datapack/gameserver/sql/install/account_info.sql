CREATE TABLE IF NOT EXISTS `account_info` (
  `account` VARCHAR(45) NOT NULL,
  `premium` TINYINT NOT NULL DEFAULT 0,
  `premium_expire` LONG NOT NULL DEFAULT 0,
  PRIMARY KEY (`account`)
)
