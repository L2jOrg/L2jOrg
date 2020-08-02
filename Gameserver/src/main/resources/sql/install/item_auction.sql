DROP TABLE IF EXISTS `item_auction`;
CREATE TABLE IF NOT EXISTS `item_auction` (
  `auctionId` int(11) NOT NULL,
  `instanceId` int(11) NOT NULL,
  `auctionItemId` int(11) NOT NULL,
  `startingTime` bigint(13) unsigned NOT NULL DEFAULT '0',
  `endingTime` bigint(13) unsigned NOT NULL DEFAULT '0',
  `auctionStateId` tinyint(1) NOT NULL,
  PRIMARY KEY (`auctionId`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;