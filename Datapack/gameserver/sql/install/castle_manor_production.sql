CREATE TABLE IF NOT EXISTS `castle_manor_production` (
	`castle_id` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`seed_id` SMALLINT UNSIGNED NOT NULL DEFAULT '0',
	`can_produce` BIGINT NOT NULL DEFAULT '0',
	`start_produce` BIGINT NOT NULL DEFAULT '0',
	`seed_price` BIGINT NOT NULL DEFAULT '0',
	`period` INT NOT NULL DEFAULT '1',
	PRIMARY KEY  (`castle_id`,`seed_id`,`period`)
) ENGINE=MyISAM;
