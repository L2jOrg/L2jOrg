/*
MySQL Data Transfer
Source Host: localhost
Source Database: l2ovdb
Target Host: localhost
Target Database: l2ovdb
Date: 30.11.2011 23:25:32
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for online
-- ----------------------------
DROP TABLE IF EXISTS `online`;
CREATE TABLE `online` (
  `index` int(1) NOT NULL,
  `totalOnline` int(6) NOT NULL,
  `totalOffline` int(6) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records 
-- ----------------------------
INSERT INTO `online` VALUES ('0', '0', '0');
