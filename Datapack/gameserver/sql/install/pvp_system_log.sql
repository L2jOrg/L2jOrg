/*
MySQL Data Transfer
Source Host: localhost
Source Database: l2ovdb
Target Host: localhost
Target Database: l2ovdb
Date: 04.12.2013 14:15:56
*/

SET FOREIGN_KEY_CHECKS=0;
-- ----------------------------
-- Table structure for pvp_system_log
-- ----------------------------
DROP TABLE IF EXISTS `pvp_system_log`;
CREATE TABLE `pvp_system_log` (
  `killer` varchar(255) NOT NULL,
  `victim` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Records 
-- ----------------------------
