DROP TABLE IF EXISTS `character_skills`;
CREATE TABLE IF NOT EXISTS `character_skills` (
  `charId` INT UNSIGNED NOT NULL DEFAULT 0,
  `skill_id` INT NOT NULL DEFAULT 0,
  `skill_level` INT(4) NOT NULL DEFAULT 1,
  `skill_sub_level` INT(4) NOT NULL DEFAULT '0',
  `class_index` INT(1) NOT NULL DEFAULT 0,
  PRIMARY KEY (`charId`,`skill_id`,`class_index`),
  FOREIGN KEY FK_CHARACTER_SKILL (`charId`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;