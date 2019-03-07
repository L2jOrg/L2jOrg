DROP TABLE IF EXISTS `character_reco_bonus`;
CREATE TABLE IF NOT EXISTS `character_reco_bonus` (
  `charId` int(10) unsigned NOT NULL,
  `rec_have` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `rec_left` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `time_left` bigint(13) unsigned NOT NULL DEFAULT '0',
  UNIQUE KEY `charId` (`charId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;