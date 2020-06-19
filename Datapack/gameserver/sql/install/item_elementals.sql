DROP TABLE IF EXISTS `item_elementals`;
CREATE TABLE IF NOT EXISTS `item_elementals` (
  `itemId` int(11) NOT NULL DEFAULT 0,
  `elemType` tinyint(1) NOT NULL DEFAULT -1,
  `elemValue` int(11) NOT NULL DEFAULT -1,
  PRIMARY KEY (`itemId`, `elemType`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;