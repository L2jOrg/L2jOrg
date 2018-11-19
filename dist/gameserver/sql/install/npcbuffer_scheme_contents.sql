/*
Navicat MySQL Data Transfer

Source Server         : l2atomic+l2destiny
Source Server Version : 50173
Source Host           : localhost:3306
Source Database       : testserver

Target Server Type    : MYSQL
Target Server Version : 50173
File Encoding         : 65001

Date: 2014-07-27 15:01:12
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for `npcbuffer_scheme_contents`
-- ----------------------------
DROP TABLE IF EXISTS `npcbuffer_scheme_contents`;
CREATE TABLE `npcbuffer_scheme_contents` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `scheme_id` int(11) DEFAULT NULL,
  `skill_id` int(8) DEFAULT NULL,
  `skill_level` int(4) DEFAULT NULL,
  `buff_class` int(2) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=MyISAM AUTO_INCREMENT=3115782 DEFAULT CHARSET=latin1;

-- ----------------------------
-- Records of npcbuffer_scheme_contents
-- ----------------------------
INSERT INTO `npcbuffer_scheme_contents` VALUES ('1396683', '43572', '1303', '2', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('1396684', '43572', '1085', '3', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('1396685', '43573', '1085', '3', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('1396686', '43573', '1303', '2', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('1396687', '43572', '1397', '3', '0');
