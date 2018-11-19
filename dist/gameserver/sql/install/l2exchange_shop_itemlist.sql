/*
MySQL Data Transfer
Source Host: 46.4.58.20
Source Database: l2jdb
Target Host: 46.4.58.20
Target Database: l2jdb
Date: 30.10.2011 1:32:09
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for l2exchange_shop_itemlist
-- ----------------------------
CREATE TABLE `l2exchange_shop_itemlist` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `itemid` int(255) NOT NULL DEFAULT '0',
  `count` int(255) DEFAULT NULL,
  `cost` int(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=8 DEFAULT CHARSET=cp1251;

-- ----------------------------
-- Records 
-- ----------------------------
