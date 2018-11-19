-- ----------------------------
-- Table structure for `character_group_reuse`
-- ----------------------------
DROP TABLE IF EXISTS `character_group_reuse`;
CREATE TABLE `character_group_reuse` (
  `object_id` int(11) NOT NULL,
  `reuse_group` int(11) NOT NULL,
  `item_id` int(11) NOT NULL,
  `end_time` bigint(20) NOT NULL,
  `reuse` bigint(20) NOT NULL,
  PRIMARY KEY (`object_id`,`reuse_group`)
);