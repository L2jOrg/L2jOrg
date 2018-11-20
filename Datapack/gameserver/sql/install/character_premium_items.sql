CREATE TABLE IF NOT EXISTS `character_premium_items` (
	`char_id` INT(11) NOT NULL,
	`receive_time` INT(11) NOT NULL,
	`item_id` INT(11) NOT NULL,
	`item_count` BIGINT(20) UNSIGNED NOT NULL,
	`sender` VARCHAR(50) CHARACTER SET UTF8 NOT NULL DEFAULT '',
	PRIMARY KEY  (`char_id`,`receive_time`,`item_id`)
);