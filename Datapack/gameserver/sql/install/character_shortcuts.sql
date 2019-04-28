DROP TABLE IF EXISTS `character_shortcuts`;
CREATE TABLE `character_shortcuts` (
  `charId` INT UNSIGNED NOT NULL DEFAULT 0,
  `slot` decimal(3) NOT NULL DEFAULT 0,
  `page` decimal(3) NOT NULL DEFAULT 0,
  `type` decimal(3) ,
  `shortcut_id` decimal(16) ,
  `level` SMALLINT,
  `sub_level` INT(4) NOT NULL DEFAULT '0',
  `class_index` int(1) NOT NULL DEFAULT '0',

  PRIMARY KEY (`charId`,`slot`,`page`,`class_index`),
  KEY `shortcut_id` (`shortcut_id`),
  FOREIGN KEY FK_FRIENDS_FRIEND (`charId`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;