CREATE TABLE IF NOT EXISTS `heroes_diary` (
  `charId` int(10) unsigned NOT NULL,
  `time` decimal(20,0) unsigned NOT NULL,
  `action` tinyint(2) unsigned NOT NULL default '0',
  `param` int(11) unsigned NOT NULL default '0',
  KEY `charId` (`charId`)
);