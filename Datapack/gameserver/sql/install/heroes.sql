CREATE TABLE IF NOT EXISTS `heroes` (
	`char_id` INT NOT NULL DEFAULT '0',
	`count` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`played` TINYINT NOT NULL DEFAULT '0',
	`active` TINYINT NOT NULL DEFAULT '0',
	`message` varchar(300) NOT NULL default '',
	PRIMARY KEY  (`char_id`)
) ENGINE=MyISAM;