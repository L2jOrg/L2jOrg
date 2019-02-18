CREATE TABLE IF NOT EXISTS `character_product_history` (
	`char_id` INT(11) NOT NULL,
	`product_id` INT(11) NOT NULL,
	`last_purchase_time` INT(11) NOT NULL,
	PRIMARY KEY  (`char_id`,`product_id`),
	FOREIGN KEY FK_PRODUCT_CHARACTER(char_id) REFERENCES characters(obj_Id) ON DELETE CASCADE
);