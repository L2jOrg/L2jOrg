CREATE TABLE IF NOT EXISTS `character_blocklist` (
	`obj_Id` INT NOT NULL,
	`target_Id` INT NOT NULL,
	`memo` VARCHAR(50) CHARACTER SET UTF8 NOT NULL DEFAULT '',
	PRIMARY KEY  (`obj_Id`,`target_Id`)
) ENGINE=MyISAM;