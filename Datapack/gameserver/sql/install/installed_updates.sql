DROP TABLE IF EXISTS `installed_updates`;
CREATE TABLE `installed_updates` (
	`file_name` VARCHAR(300) CHARACTER SET UTF8 NOT NULL DEFAULT '0',
	PRIMARY KEY  (`file_name`)
) ENGINE=MyISAM;