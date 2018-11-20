CREATE TABLE IF NOT EXISTS `character_shortcuts` (
	`object_id` INT(11) NOT NULL DEFAULT '0',
	`slot` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`page` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`type` TINYINT UNSIGNED,
	`shortcut_id` int,
	`level` SMALLINT,
	`class_index` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`character_type` INT(11) NOT NULL DEFAULT '1',
	PRIMARY KEY (`object_id`,`slot`,`page`,`class_index`),
	KEY `shortcut_id` (`shortcut_id`)
) ENGINE=MyISAM;