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
