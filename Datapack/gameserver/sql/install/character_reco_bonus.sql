DROP TABLE IF EXISTS `character_reco_bonus`;
CREATE TABLE IF NOT EXISTS `character_reco_bonus` (
  `charId` INT unsigned NOT NULL,
  `rec_have` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `rec_left` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `time_left` bigint(13) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY  `charId` (`charId`),
  FOREIGN KEY (`charId`) REFERENCES characters (`charId`) ON DELETE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;