DROP TABLE IF EXISTS `character_instance_time`;
CREATE TABLE IF NOT EXISTS `character_instance_time` (
  `charId` INT UNSIGNED NOT NULL DEFAULT '0',
  `instanceId` int(3) NOT NULL DEFAULT '0',
  `time` bigint(13) unsigned NOT NULL DEFAULT '0',

  PRIMARY KEY (`charId`,`instanceId`),
  FOREIGN KEY FK_CHARACTER_INSTANCE (`charId`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;