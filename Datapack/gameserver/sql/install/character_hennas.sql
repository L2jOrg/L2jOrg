CREATE TABLE IF NOT EXISTS `character_hennas` (
	`char_obj_id` INT NOT NULL DEFAULT '0',
	`symbol_id` SMALLINT UNSIGNED NOT NULL DEFAULT '0',
	`class_index` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`draw_time` INT UNSIGNED NOT NULL DEFAULT '0',
	`is_premium` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	PRIMARY KEY  (`char_obj_id`,`class_index`,`draw_time`)
) ENGINE=MyISAM;