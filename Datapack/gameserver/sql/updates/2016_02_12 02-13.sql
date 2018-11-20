DROP TABLE IF EXISTS `pa_free_table`;
DROP TABLE IF EXISTS `premium_account_table`;
RENAME TABLE `account_bonus` TO `premium_accounts`;
ALTER TABLE `premium_accounts` CHANGE `bonus` `type` double NOT NULL;
ALTER TABLE `premium_accounts` CHANGE `bonus_expire` `expire_time` int(11) NOT NULL;