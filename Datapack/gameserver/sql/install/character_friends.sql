CREATE TABLE IF NOT EXISTS `character_friends` (
	`char_id` INT NOT NULL DEFAULT '0',
	`friend_id` INT NOT NULL DEFAULT '0',
	`memo` VARCHAR(50) CHARACTER SET UTF8 NOT NULL DEFAULT '',
	PRIMARY KEY  (`char_id`,`friend_id`)
) ENGINE=MyISAM;