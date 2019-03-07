DROP TABLE IF EXISTS `fort_siege_guards`;
CREATE TABLE IF NOT EXISTS `fort_siege_guards` (
  `fortId` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `id` smallint(4) unsigned NOT NULL AUTO_INCREMENT,
  `npcId` smallint(5) unsigned NOT NULL DEFAULT '0',
  `x` mediumint(6) NOT NULL DEFAULT '0',
  `y` mediumint(6) NOT NULL DEFAULT '0',
  `z` mediumint(6) NOT NULL DEFAULT '0',
  `heading` mediumint(6) NOT NULL DEFAULT '0',
  `respawnDelay` mediumint(5) NOT NULL DEFAULT '0',
  `isHired` tinyint(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `id` (`fortId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;