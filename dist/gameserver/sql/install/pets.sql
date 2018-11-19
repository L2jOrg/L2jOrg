CREATE TABLE IF NOT EXISTS `pets` (
	`item_obj_id` INT NOT NULL DEFAULT '0',
	`objId` int,
	`name` VARCHAR(12) CHARACTER SET UTF8 DEFAULT NULL,
	`curHp` mediumint UNSIGNED,
	`curMp` mediumint UNSIGNED,
	`exp` bigint,
	`sp` INT UNSIGNED,
	`fed` INT UNSIGNED,
	`max_fed` SMALLINT UNSIGNED,
	PRIMARY KEY (item_obj_id)
) ENGINE=MyISAM;