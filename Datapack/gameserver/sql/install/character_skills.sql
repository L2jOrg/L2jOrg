CREATE TABLE IF NOT EXISTS `character_skills` (
	`char_obj_id` INT NOT NULL DEFAULT '0',
	`skill_id` SMALLINT UNSIGNED NOT NULL DEFAULT '0',
	`skill_level` SMALLINT UNSIGNED NOT NULL DEFAULT '0',
	`class_index` SMALLINT NOT NULL DEFAULT '0',
	PRIMARY KEY  (`char_obj_id`,`skill_id`,`class_index`)
) ENGINE=MyISAM;