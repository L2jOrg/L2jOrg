DROP TABLE IF EXISTS `character_offline_trade`;
CREATE TABLE IF NOT EXISTS `character_offline_trade` (
  `charId` int(10) unsigned NOT NULL,
  `time` bigint(13) unsigned NOT NULL DEFAULT '0',
  `type` tinyint(4) NOT NULL DEFAULT '0',
  `title` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`charId`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;