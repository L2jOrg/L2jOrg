DROP TABLE IF EXISTS `item_auction_bid`;
CREATE TABLE IF NOT EXISTS `item_auction_bid` (
  `auctionId` int(11) NOT NULL,
  `playerObjId` int(11) NOT NULL,
  `playerBid` bigint(20) NOT NULL,
  PRIMARY KEY (`auctionId`,`playerObjId`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;