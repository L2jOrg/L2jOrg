/*
MySQL Data Transfer
Source Host: 46.4.58.20
Source Database: l2jdb
Target Host: 46.4.58.20
Target Database: l2jdb
Date: 30.10.2011 1:32:26
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for l2scripts_ban_ip
-- ----------------------------
CREATE TABLE `l2scripts_ban_ip` (
  `type` varchar(255) NOT NULL,
  `ip` varchar(20) NOT NULL,
  `time` varchar(255) DEFAULT NULL
) ENGINE=MyISAM DEFAULT CHARSET=cp1251 ROW_FORMAT=DYNAMIC;

-- ----------------------------
-- Records 
-- ----------------------------
