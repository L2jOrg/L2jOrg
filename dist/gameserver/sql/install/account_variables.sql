CREATE TABLE IF NOT EXISTS `account_variables` (
  `account_name` VARCHAR(45) NOT NULL DEFAULT '',
  `var`  VARCHAR(50) NOT NULL DEFAULT '',
  `value` VARCHAR(255) ,
  PRIMARY KEY (`account_name`,`var`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
