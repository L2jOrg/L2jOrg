CREATE TABLE IF NOT EXISTS `raidboss_status` (
	`id` INT NOT NULL,
	`current_hp` INT DEFAULT NULL,
	`current_mp` INT DEFAULT NULL,
	`death_time` INT NOT NULL DEFAULT '0',
	`respawn_delay` INT NOT NULL DEFAULT '0',
	PRIMARY KEY  (`id`)
) ENGINE=MyISAM;