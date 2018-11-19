DROP TABLE IF EXISTS `custom_heroes`;
CREATE TABLE `custom_heroes` (
  `hero_id` int(11) NOT NULL,
  `time` int(11) NOT NULL,
  PRIMARY KEY (`hero_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;