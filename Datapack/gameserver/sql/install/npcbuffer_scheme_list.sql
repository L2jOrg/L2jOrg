-- ----------------------------
-- Table structure for `npcbuffer_scheme_list`
-- ----------------------------
DROP TABLE IF EXISTS `npcbuffer_scheme_list`;
CREATE TABLE `npcbuffer_scheme_list` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `player_id` varchar(40) DEFAULT NULL,
  `scheme_name` varchar(36) DEFAULT NULL,
  `mod_accepted` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`id`)
);

-- ----------------------------
-- Records of npcbuffer_scheme_list
-- ----------------------------
INSERT INTO `npcbuffer_scheme_list` VALUES ('43572', '268482203', 'buff', null);
INSERT INTO `npcbuffer_scheme_list` VALUES ('43573', '268481735', 'lips', null);
INSERT INTO `npcbuffer_scheme_list` VALUES ('43574', '268483826', 'poleur', null);
INSERT INTO `npcbuffer_scheme_list` VALUES ('43575', '268481992', 'Arcos', null);
INSERT INTO `npcbuffer_scheme_list` VALUES ('43576', '268481926', '12', null);
INSERT INTO `npcbuffer_scheme_list` VALUES ('43577', '268488037', '1111111111', null);
INSERT INTO `npcbuffer_scheme_list` VALUES ('43578', '268489076', 'mag', null);
INSERT INTO `npcbuffer_scheme_list` VALUES ('43579', '268486308', 'PvE', null);
