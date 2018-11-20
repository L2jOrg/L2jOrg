/*
MySQL Data Transfer
Source Host: 46.4.58.20
Source Database: l2jdb
Target Host: 46.4.58.20
Target Database: l2jdb
Date: 30.10.2011 1:32:16
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for l2exchange_shop_log
-- ----------------------------
CREATE TABLE `l2exchange_shop_log` (
  `id` int(255) NOT NULL AUTO_INCREMENT,
  `account` varchar(255) DEFAULT NULL,
  `ip` varchar(255) DEFAULT NULL,
  `comment` text,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=2767 DEFAULT CHARSET=cp1251;

-- ----------------------------
-- Records 
-- ----------------------------
