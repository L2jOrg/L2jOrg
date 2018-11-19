/*
MySQL Data Transfer
Source Host: 46.4.58.20
Source Database: l2jdb
Target Host: 46.4.58.20
Target Database: l2jdb
Date: 30.10.2011 1:31:54
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for l2exchange_shop
-- ----------------------------
CREATE TABLE `l2exchange_shop` (
  `account` varchar(255) NOT NULL,
  `count` int(255) DEFAULT NULL,
  PRIMARY KEY (`account`)
) ENGINE=MyISAM DEFAULT CHARSET=cp1251;

-- ----------------------------
-- Records 
-- ----------------------------
