CREATE TABLE IF NOT EXISTS `character_effects_save` (
	`object_id` INT NOT NULL,
	`skill_id` INT NOT NULL,
	`skill_level` INT NOT NULL,
	`duration` INT NOT NULL,
	`left_time` INT NOT NULL,
	`id` INT NOT NULL,
	`is_self` TINYINT(1) NOT NULL,
	PRIMARY KEY (`object_id`,`skill_id`,`id`,`is_self`)
) ENGINE=MyISAM;