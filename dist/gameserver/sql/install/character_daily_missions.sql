CREATE TABLE IF NOT EXISTS `character_daily_missions` (
	`char_id` INT(11) NOT NULL,
	`mission_id` INT(11) NOT NULL,
	`completed` TINYINT(1) NOT NULL,
	`value` INT(11) NOT NULL,
	PRIMARY KEY  (`char_id`,`mission_id`)
);