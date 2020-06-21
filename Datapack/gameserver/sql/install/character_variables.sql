DROP TABLE IF EXISTS `character_variables`;
CREATE TABLE IF NOT EXISTS `character_variables` (
  `charId` INT UNSIGNED NOT NULL,
  `var` varchar(255) NOT NULL,
  `val` text NOT NULL,
  FOREIGN KEY (`charId`) REFERENCES characters (`charId`) ON DELETE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;