DROP TABLE IF EXISTS `clan_skills`;
CREATE TABLE IF NOT EXISTS `clan_skills` (
  `clan_id` int(11) NOT NULL DEFAULT 0,
  `skill_id` int(11) NOT NULL DEFAULT 0,
  `skill_level` int(5) NOT NULL DEFAULT 0,
  `skill_name` varchar(26) DEFAULT NULL,
  `sub_pledge_id` INT NOT NULL DEFAULT '-2',
  PRIMARY KEY (`clan_id`,`skill_id`,`sub_pledge_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;