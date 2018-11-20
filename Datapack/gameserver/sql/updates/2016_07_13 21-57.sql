CREATE TABLE IF NOT EXISTS `items_to_delete` (
	`item_id` SMALLINT UNSIGNED NOT NULL,
	`description` VARCHAR(255) DEFAULT "",
	PRIMARY KEY (`item_id`, `description`)
) ENGINE=MyISAM;