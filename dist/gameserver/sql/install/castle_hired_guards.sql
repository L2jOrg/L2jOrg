CREATE TABLE IF NOT EXISTS `castle_hired_guards` (
  `residence_id` int(11) NOT NULL DEFAULT '0',
  `item_id` int(11) NOT NULL DEFAULT '0',
  `x` int(11) NOT NULL DEFAULT '0',
  `y` int(11) NOT NULL DEFAULT '0',
  `z` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`residence_id`,`item_id`,`x`,`y`, `z`),
  KEY `id` (`residence_id`)
) ;
