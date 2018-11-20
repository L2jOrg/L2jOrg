CREATE TABLE IF NOT EXISTS `character_variables` (
	`obj_id` INT NOT NULL DEFAULT '0',
	`name` VARCHAR(86) CHARACTER SET UTF8 NOT NULL DEFAULT '0',
	`value` VARCHAR(300) CHARACTER SET UTF8 NOT NULL DEFAULT '0',
	`expire_time` bigint(20) NOT NULL DEFAULT '0',
	UNIQUE KEY `prim` (`obj_id`,`name`),
	KEY `obj_id` (`obj_id`),
	KEY `name` (`name`),
	KEY `value` (`value`),
	KEY `expire_time` (`expire_time`)
) ENGINE=MyISAM;
