DROP TABLE IF EXISTS `item_auction`;
CREATE TABLE IF NOT EXISTS `item_auction` (
  `auction` INT NOT NULL,
  `instance` INT NOT NULL,
  `auction_item` INT NOT NULL,
  `starting_time` BIGINT UNSIGNED NOT NULL DEFAULT '0',
  `ending_time`  BIGINT UNSIGNED NOT NULL DEFAULT '0',
  `auction_state` ENUM('CREATED', 'STARTED', 'FINISHED') NOT NULL,
  PRIMARY KEY (auction),
  KEY (`instance`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;