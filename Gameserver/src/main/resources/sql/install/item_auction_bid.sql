DROP TABLE IF EXISTS `item_auction_bid`;
CREATE TABLE IF NOT EXISTS `item_auction_bid` (
  `auction` INT NOT NULL,
  `player_id` INT NOT NULL,
  `bid` BIGINT NOT NULL,
  PRIMARY KEY (`auction`,`player_id`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;