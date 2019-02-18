CREATE TABLE IF NOT EXISTS  `premium_accounts` (
  `account` varchar(45) NOT NULL,
  `type` double NOT NULL,
  `expire_time` int(11) NOT NULL,
  PRIMARY KEY (`account`),
  FOREIGN KEY (`account`)  REFERENCES  characters(account_name) ON DELETE CASCADE
);