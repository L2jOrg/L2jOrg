CREATE TABLE IF NOT EXISTS `couples` (
	`id` INT NOT NULL,
	`player1Id` INT NOT NULL DEFAULT '0',
	`player2Id` INT NOT NULL DEFAULT '0',
	`maried` VARCHAR(5) DEFAULT NULL,
	`affiancedDate` bigint DEFAULT '0',
	`weddingDate` bigint DEFAULT '0',
	PRIMARY KEY  (`id`)
) ENGINE=MyISAM;