DROP TABLE IF EXISTS `itemsonground`;
CREATE TABLE IF NOT EXISTS `itemsonground` (
  `object_id` int(11) NOT NULL DEFAULT '0',
  `item_id` int(11) DEFAULT NULL,
  `count` BIGINT UNSIGNED NOT NULL DEFAULT 0,
  `enchant_level` int(11) DEFAULT NULL,
  `x` int(11) DEFAULT NULL,
  `y` int(11) DEFAULT NULL,
  `z` int(11) DEFAULT NULL,
  `drop_time` bigint(13) NOT NULL DEFAULT '0',
  `equipable` int(1) DEFAULT '0',
  PRIMARY KEY (`object_id`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;