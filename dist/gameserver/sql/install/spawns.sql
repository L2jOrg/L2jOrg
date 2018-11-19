CREATE TABLE IF NOT EXISTS `spawns` (
	`npc_id` SMALLINT UNSIGNED NOT NULL,
	`x` INT NOT NULL,
	`y` INT NOT NULL,
	`z` INT NOT NULL,
	`heading` INT NOT NULL,
	`respawn` INT NOT NULL,
	`count` INT NOT NULL,
	PRIMARY KEY  (`npc_id`, `x`, `y`, `z`)
) ENGINE=MyISAM;