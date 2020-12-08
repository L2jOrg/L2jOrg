DROP TABLE IF EXISTS `character_hennas`;
CREATE TABLE IF NOT EXISTS `character_hennas` (
  `charId` INT UNSIGNED NOT NULL DEFAULT 0,
  `symbol_id` INT,
  `slot` INT NOT NULL DEFAULT 0,
  PRIMARY KEY (`charId`,`slot`),
  FOREIGN KEY FK_CHARACTER_HENNA (`charId`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;