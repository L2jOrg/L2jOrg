DROP TABLE IF EXISTS `bot_reported_char_data`;
CREATE TABLE IF NOT EXISTS `bot_reported_char_data` (
	`botId` INT UNSIGNED NOT NULL DEFAULT 0,
	`reporterId` INT UNSIGNED NOT NULL DEFAULT 0,
	`reportDate` BIGINT(13) unsigned NOT NULL DEFAULT '0',
	PRIMARY KEY (`botId`, `reporterId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8; 