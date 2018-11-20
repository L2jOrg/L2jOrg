CREATE TABLE IF NOT EXISTS  `premium_accounts` (
  `account` varchar(255) NOT NULL,
  `type` double NOT NULL,
  `expire_time` int(11) NOT NULL,
  PRIMARY KEY (`account`)
);