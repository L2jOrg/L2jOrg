CREATE TABLE IF NOT EXISTS `items_delayed` (
	`payment_id` INT NOT NULL auto_increment,
	`owner_id` INT NOT NULL,
	`item_id` SMALLINT UNSIGNED NOT NULL,
	`count` bigint(20) NOT NULL DEFAULT '1',
	`enchant_level` SMALLINT UNSIGNED NOT NULL DEFAULT '0',
	`attribute` SMALLINT NOT NULL DEFAULT '-1',
	`attribute_level` SMALLINT NOT NULL DEFAULT '-1',
	`flags` INT NOT NULL DEFAULT '0',
	`payment_status` TINYINT UNSIGNED NOT NULL DEFAULT '0',
	`description` VARCHAR(255) DEFAULT NULL,
	PRIMARY KEY (`payment_id`),
	KEY `key_owner_id` (`owner_id`),
	KEY `key_item_id` (`item_id`)
) ENGINE=MyISAM;