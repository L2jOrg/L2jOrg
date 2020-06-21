DROP TABLE IF EXISTS `character_tpbookmark`;
CREATE TABLE IF NOT EXISTS `character_tpbookmark` (
  `charId` int(20) NOT NULL,
  `Id` int(20) NOT NULL,
  `x` int(20) NOT NULL,
  `y` int(20) NOT NULL,
  `z` int(20) NOT NULL,
  `icon` int(20) NOT NULL,
  `tag` varchar(50) DEFAULT NULL,
  `name` varchar(50) NOT NULL,
  PRIMARY KEY (`charId`,`Id`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;