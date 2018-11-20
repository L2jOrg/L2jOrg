CREATE TABLE IF NOT EXISTS `character_summons_save` (
	`owner_obj_id` INT UNSIGNED,
	`skill_id` SMALLINT UNSIGNED NOT NULL,
	`skill_level` SMALLINT UNSIGNED NOT NULL,
	`curHp` MEDIUMINT UNSIGNED,
	`curMp` MEDIUMINT UNSIGNED,
	`time` INT UNSIGNED NOT NULL,
	PRIMARY KEY (owner_obj_id, skill_id)
) ENGINE=MyISAM;