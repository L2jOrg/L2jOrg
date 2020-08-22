DROP TABLE IF EXISTS `character_premium_items`;
CREATE TABLE IF NOT EXISTS `character_premium_items` (
  `charId` int(11) NOT NULL,
  `itemNum` int(11) NOT NULL,
  `itemId` int(11) NOT NULL,
  `itemCount` bigint(20) unsigned NOT NULL,
  `itemSender` varchar(50) NOT NULL,
  KEY `charId` (`charId`),
  KEY `itemNum` (`itemNum`),
  KEY `itemId` (`itemId`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;