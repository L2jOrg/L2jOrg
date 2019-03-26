DROP TABLE IF EXISTS `fort_spawnlist`;
CREATE TABLE `fort_spawnlist` (
  `fortId` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `id` smallint(4) unsigned NOT NULL AUTO_INCREMENT,
  `npcId` smallint(5) unsigned NOT NULL DEFAULT '0',
  `x` mediumint(6) NOT NULL DEFAULT '0',
  `y` mediumint(6) NOT NULL DEFAULT '0',
  `z` mediumint(6) NOT NULL DEFAULT '0',
  `heading` mediumint(6) NOT NULL DEFAULT '0',
  `spawnType` tinyint(1) unsigned NOT NULL DEFAULT '0', -- 0-always spawned, 1-despawned during siege, 2-despawned 10min before siege, 3-spawned after fort taken
  `castleId` tinyint(1) unsigned NOT NULL DEFAULT '0',  -- Castle ID for Special Envoys
  PRIMARY KEY (`id`),
  KEY `id` (`fortId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;
