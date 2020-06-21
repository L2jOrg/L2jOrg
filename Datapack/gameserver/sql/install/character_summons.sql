DROP TABLE IF EXISTS `character_summons`;
CREATE TABLE IF NOT EXISTS `character_summons` (
  `ownerId` int(10) unsigned NOT NULL,
  `summonId` int(10) unsigned NOT NULL,
  `summonSkillId` int(10) unsigned NOT NULL,
  `curHp` int(9) unsigned DEFAULT '0',
  `curMp` int(9) unsigned DEFAULT '0',
  `time` int(10) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`ownerId`,`summonSkillId`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;