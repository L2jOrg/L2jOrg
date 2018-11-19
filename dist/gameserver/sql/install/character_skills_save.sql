CREATE TABLE IF NOT EXISTS `character_skills_save` (
	`char_obj_id` INT NOT NULL DEFAULT '0',
	`skill_id` INT UNSIGNED NOT NULL DEFAULT '0',
	`skill_level` SMALLINT UNSIGNED NOT NULL DEFAULT '0',
	`class_index` SMALLINT NOT NULL DEFAULT '0',
	`end_time` bigint NOT NULL DEFAULT '0',
	`reuse_delay_org` INT NOT NULL DEFAULT '0',
	PRIMARY KEY  (`char_obj_id`,`skill_id`,`class_index`)
) ENGINE=MyISAM;