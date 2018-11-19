CREATE TABLE IF NOT EXISTS `vote` (
	`id` INT(10) NOT NULL DEFAULT '0',
	`HWID` VARCHAR(32) NOT NULL DEFAULT '',
	`vote` INT(10) NOT NULL DEFAULT '0',
	PRIMARY KEY (`id`, `HWID`, `vote`),
	INDEX `Index 2` (`id`, `vote`),
	INDEX `Index 3` (`id`),
	INDEX `Index 4` (`HWID`)
) ENGINE=MyISAM;