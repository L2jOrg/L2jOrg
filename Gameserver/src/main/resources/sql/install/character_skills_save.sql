DROP TABLE IF EXISTS `character_skills_save`;
CREATE TABLE IF NOT EXISTS `character_skills_save` (
  `charId` INT UNSIGNED NOT NULL DEFAULT 0,
  `skill_id` INT NOT NULL DEFAULT 0,
  `skill_level` INT NOT NULL DEFAULT 1,
  `skill_sub_level` INT NOT NULL DEFAULT '0',
  `remaining_time` INT NOT NULL DEFAULT 0,
  `reuse_delay` INT NOT NULL DEFAULT 0,
  `systime` BIGINT UNSIGNED NOT NULL DEFAULT '0',
  `restore_type` INT NOT NULL DEFAULT 0,
  `buff_index` INT NOT NULL DEFAULT 0,

  PRIMARY KEY (`charId`,`skill_id`,`skill_level`),
  FOREIGN KEY FK_CHARACTER_SKILL_SAVE (`charId`) REFERENCES characters (`charId`) ON DELETE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;