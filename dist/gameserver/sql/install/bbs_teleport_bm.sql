/*
MySQL Data Transfer
Source Host: localhost
Source Database: l2ovdb
Target Host: localhost
Target Database: l2ovdb
Date: 23.10.2011 3:04:18
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for bbs_teleport_bm
-- ----------------------------
DROP TABLE IF EXISTS `bbs_teleport_bm`;
CREATE TABLE `bbs_teleport_bm` (
  `char_id` int(10) NOT NULL,
  `name` varchar(255) NOT NULL,
  `x` mediumint(9) NOT NULL,
  `y` mediumint(9) NOT NULL,
  `z` mediumint(9) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records 
-- ----------------------------
