CREATE TABLE `mmotop_winners` (
  `id` int(255) NOT NULL DEFAULT '0',
  `name` varchar(255) NOT NULL DEFAULT '',
  `prizid` int(11) DEFAULT NULL,
  `data` date DEFAULT NULL,
  `time` time DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

INSERT INTO `mmotop_winners` VALUES ('0', 'Admin', '15', '2009-12-23', '00:00:00');
