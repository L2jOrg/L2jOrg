CREATE TABLE IF NOT EXISTS `custom_item_auction` (
  `auctionId` int(11) NOT NULL,
  `auctionItemId` int(11) NOT NULL,
  `itemId` int(7) NOT NULL,
  `itemCount` bigint(20) NOT NULL,
  `itemEnchant` int(11) NOT NULL,
  `bidItemId` int(7) NOT NULL,
  `bidItemStartCount` bigint(20) NOT NULL,
  PRIMARY KEY (`auctionId`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;