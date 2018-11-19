CREATE TABLE IF NOT EXISTS `character_mentoring` (
	`mentor` INT NOT NULL DEFAULT '0',
	`mentee` INT NOT NULL DEFAULT '0',
	PRIMARY KEY  (`mentor`,`mentee`)
) ENGINE=MyISAM;