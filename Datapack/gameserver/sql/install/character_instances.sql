CREATE TABLE IF NOT EXISTS `character_instances` (
	`obj_id` INT NOT NULL DEFAULT '0',
	`id` INT NOT NULL DEFAULT '0',
	`reuse` BIGINT(20) NOT NULL DEFAULT '0',
	UNIQUE KEY `prim` (`obj_id`,`id`),
	KEY `obj_id` (`obj_id`)
) ENGINE=MyISAM;