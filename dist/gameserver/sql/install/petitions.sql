CREATE TABLE IF NOT EXISTS `petitions` (
	`serv_id` TINYINT(3) UNSIGNED NOT NULL DEFAULT '0',
	`act_time` int(10) UNSIGNED NOT NULL DEFAULT '0',
	`petition_type` TINYINT(3) UNSIGNED NOT NULL DEFAULT '0',
	`actor` int(10) UNSIGNED NOT NULL DEFAULT '0',
	`location_x` mediumint(9) DEFAULT NULL,
	`location_y` mediumint(9) DEFAULT NULL,
	`location_z` SMALLINT(6) DEFAULT NULL,
	`petition_text` text CHARACTER SET UTF8 NOT NULL,
	`STR_actor` VARCHAR(50) CHARACTER SET UTF8 DEFAULT NULL,
	`STR_actor_account` VARCHAR(50) CHARACTER SET UTF8 DEFAULT NULL,
	`petition_status` TINYINT(3) UNSIGNED NOT NULL DEFAULT '0',
	KEY `actor` (`actor`),
	KEY `petition_status` (`petition_status`),
	KEY `petition_type` (`petition_type`),
	KEY `serv_id` (`serv_id`)
) ENGINE=MyISAM;