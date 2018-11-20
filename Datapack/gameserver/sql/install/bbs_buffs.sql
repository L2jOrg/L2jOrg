DROP TABLE IF EXISTS `bbs_buffs`;
CREATE TABLE `bbs_buffs` (
	`id` int(11) NOT NULL auto_increment,
	`char_id` int(11) NOT NULL DEFAULT '0',
	`name` varchar(256) CHARACTER SET UTF8 NOT NULL DEFAULT '',
	`skills` varchar(256) NOT NULL DEFAULT '',
	PRIMARY KEY (`id`, `char_id`,`name`)
) ENGINE=MyISAM;

-- ----------------------------
-- Records 
-- ----------------------------
INSERT INTO `bbs_buffs` (char_id, name, skills) VALUES (0, 'Fighter;Воину', '1068,1040,1204,1077,1062,1087,1044,1268,1240,1242,1048,1045,1036,1086,1388,1397');
INSERT INTO `bbs_buffs` (char_id, name, skills) VALUES (0, 'Mystic;Магу', '1040,1078,1204,1085,1062,1087,1044,1059,1048,1045,1036,1389,1397,1303');
INSERT INTO `bbs_buffs` (char_id, name, skills) VALUES (0, 'Resistance;Сопротивление', '1035,1033,1032,1392,1393,1259,270');
INSERT INTO `bbs_buffs` (char_id, name, skills) VALUES (0, 'Dance\'s / Song\'s;Песни / Танцы', '267,270,268,269,265,304,264,266,305,274,277,272,273,276,271,275,311,310');
INSERT INTO `bbs_buffs` (char_id, name, skills) VALUES (0, 'Maximum speed;Максимум скорости', '1204,1062,268');
