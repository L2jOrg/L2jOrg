/*
MySQL Data Transfer
Source Host: 46.4.58.20
Source Database: l2jdb
Target Host: 46.4.58.20
Target Database: l2jdb
Date: 30.10.2011 1:32:35
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for l2scripts_log
-- ----------------------------
CREATE TABLE `l2scripts_log` (
  `ip` varchar(255) NOT NULL,
  `date` datetime NOT NULL DEFAULT '0000-00-00 00:00:00',
  `nick` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  `param` varchar(255) NOT NULL
) ENGINE=MyISAM DEFAULT CHARSET=cp1251;

-- ----------------------------
-- Records 
-- ----------------------------
