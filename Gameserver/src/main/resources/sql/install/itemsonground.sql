DROP TABLE IF EXISTS `itemsonground`;
CREATE TABLE IF NOT EXISTS `itemsonground` (
  `object_id` INT NOT NULL DEFAULT '0',
  `item_id` INT DEFAULT NULL,
  `count` BIGINT UNSIGNED NOT NULL DEFAULT 0,
  `enchant_level` INT DEFAULT NULL,
  `x` INT DEFAULT NULL,
  `y` INT DEFAULT NULL,
  `z` INT DEFAULT NULL,
  `drop_time` BIGINT NOT NULL DEFAULT '0',
  `equipable` INT DEFAULT '0',
  `ensoul` INT,
  `special_ensoul` INT,
  PRIMARY KEY (`object_id`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;