DROP TABLE IF EXISTS `account_gsdata`;
CREATE TABLE IF NOT EXISTS `account_gsdata` (
  `account_name` VARCHAR(45) NOT NULL DEFAULT '',
  `var`  VARCHAR(255) NOT NULL DEFAULT '',
  `value` text NOT NULL,
  PRIMARY KEY (`account_name`,`var`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;
