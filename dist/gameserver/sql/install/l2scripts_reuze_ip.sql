/*
MySQL Data Transfer
Source Host: 46.4.58.20
Source Database: l2jdb
Target Host: 46.4.58.20
Target Database: l2jdb
Date: 30.10.2011 1:32:52
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for l2scripts_reuze_ip
-- ----------------------------
CREATE TABLE `l2scripts_reuze_ip` (
  `type` text NOT NULL,
  `ip` text NOT NULL,
  `time` int(11) NOT NULL DEFAULT '0'
) ENGINE=MyISAM DEFAULT CHARSET=cp1251;

-- ----------------------------
-- Records 
-- ----------------------------
