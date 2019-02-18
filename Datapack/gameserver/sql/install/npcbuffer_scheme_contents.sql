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
);

-- ----------------------------
-- Records of npcbuffer_scheme_contents
-- ----------------------------
INSERT INTO `npcbuffer_scheme_contents` VALUES ('1396683', '43572', '1303', '2', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('1396684', '43572', '1085', '3', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('1396685', '43573', '1085', '3', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('1396686', '43573', '1303', '2', '0');
INSERT INTO `npcbuffer_scheme_contents` VALUES ('1396687', '43572', '1397', '3', '0');
