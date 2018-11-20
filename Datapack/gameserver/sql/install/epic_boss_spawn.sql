CREATE TABLE IF NOT EXISTS `epic_boss_spawn` (
	`bossId` SMALLINT UNSIGNED NOT NULL,
	`respawnDate` INT NOT NULL,
	`state` INT NOT NULL,
	PRIMARY KEY  (`bossId`)
) ENGINE=MyISAM;