DROP TABLE IF EXISTS `pets`;
CREATE TABLE IF NOT EXISTS `pets` (
  `item_obj_id` int(10) unsigned NOT NULL,
  `name` varchar(16),
  `level` smallint(2) unsigned NOT NULL,
  `curHp` int(9) unsigned DEFAULT '0',
  `curMp` int(9) unsigned DEFAULT '0',
  `exp` bigint(20) unsigned DEFAULT '0',
  `sp` bigint(10) unsigned DEFAULT '0',
  `fed` int(10) unsigned DEFAULT '0',
  `ownerId` int(10) NOT NULL DEFAULT '0',
  `restore` enum('true','false') NOT NULL DEFAULT 'false',
  PRIMARY KEY (`item_obj_id`),
  KEY `ownerId` (`ownerId`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;