CREATE TABLE `mmotop_prizi` (
  `prizid` int(255) NOT NULL DEFAULT '0',
  `itemid` int(10) NOT NULL DEFAULT '0',
  `kolvo` int(10) DEFAULT NULL,
  `kolvo-mes` varchar(255) DEFAULT NULL,
  `rozdano` int(10) DEFAULT NULL,
  `chance` int(3) DEFAULT NULL,
  PRIMARY KEY (`prizid`)
) ENGINE=MyISAM DEFAULT CHARSET=utf8;

INSERT INTO `mmotop_prizi` VALUES ('0', '6673', '5', 'unlimit', '0', '100');
