CREATE TABLE IF NOT EXISTS `ally_data` (
	`ally_id` INT NOT NULL DEFAULT '0',
	`ally_name` VARCHAR(45) CHARACTER SET UTF8 DEFAULT NULL,
	`leader_id` INT NOT NULL DEFAULT '0',
	`expelled_member` INT UNSIGNED NOT NULL DEFAULT '0',
	`crest` VARBINARY(192) NULL DEFAULT NULL,
	PRIMARY KEY  (`ally_id`),
	KEY `leader_id` (`leader_id`)
) ENGINE=MyISAM;
