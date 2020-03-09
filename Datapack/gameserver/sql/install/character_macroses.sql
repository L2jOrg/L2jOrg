DROP TABLE IF EXISTS `character_macroses`;
CREATE TABLE IF NOT EXISTS `character_macroses` (
  `charId` INT UNSIGNED NOT NULL DEFAULT 0,
  `id` INT NOT NULL DEFAULT 0,
  `icon` INT,
  `name` VARCHAR(40) ,
  `descr` VARCHAR(80) ,
  `acronym` VARCHAR(4) ,
  `commands` VARCHAR(500) ,

  PRIMARY KEY (`charId`,`id`),
  FOREIGN KEY FK_CHARACTER_MACRO (`charId`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;