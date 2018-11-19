CREATE TABLE `mmotop_voted_ip` (
  `id` int(10) NOT NULL DEFAULT '0',
  `charid` int(255) DEFAULT NULL,
  `charname` varchar(255) DEFAULT NULL,
  `ip` varchar(25) DEFAULT NULL,
  `date_vote` date DEFAULT '0000-00-00',
  `time_vote` time DEFAULT NULL,
  `date_deliver` date DEFAULT NULL,
  `time_deliver` time DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

INSERT INTO `mmotop_voted_ip` VALUES ('0', '123123', '123123', '123.123.123.123', '2009-07-08', '00:00:00', '2009-07-19', '00:00:00');
