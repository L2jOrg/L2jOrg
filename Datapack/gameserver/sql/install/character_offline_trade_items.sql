DROP TABLE IF EXISTS `character_offline_trade_items`;
CREATE TABLE IF NOT EXISTS `character_offline_trade_items` (
  `charId` int(10) unsigned NOT NULL,
  `item` int(10) unsigned NOT NULL DEFAULT '0', -- itemId(for buy) & ObjectId(for sell)
  `count` bigint(20) unsigned NOT NULL DEFAULT '0',
  `price` bigint(20) unsigned NOT NULL DEFAULT '0',
  KEY `charId` (`charId`),
  KEY `item` (`item`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;