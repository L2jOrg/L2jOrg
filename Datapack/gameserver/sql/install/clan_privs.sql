CREATE TABLE IF NOT EXISTS `clan_privs` (
	`clan_id` INT NOT NULL DEFAULT '0',
	`rank` INT NOT NULL DEFAULT '0',
	`privilleges` INT NOT NULL DEFAULT '0',
	PRIMARY KEY  (`clan_id`,`rank`),
	FOREIGN KEY FK_PRIVS_CLAN(clan_id) REFERENCES clan_data(clan_id) ON DELETE CASCADE
);
