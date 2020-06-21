DROP TABLE IF EXISTS `heroes_diary`;
CREATE TABLE IF NOT EXISTS `heroes_diary` (
  `charId` int(10) unsigned NOT NULL,
  `time` bigint(13) unsigned NOT NULL DEFAULT '0',
  `action` tinyint(2) unsigned NOT NULL DEFAULT '0',
  `param` int(11) unsigned NOT NULL DEFAULT '0',
  KEY `charId` (`charId`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;