DROP TABLE IF EXISTS `clan_skills`;
CREATE TABLE IF NOT EXISTS `clan_skills` (
  `clan_id` INT NOT NULL DEFAULT 0,
  `skill_id` INT NOT NULL DEFAULT 0,
  `skill_level` INT NOT NULL DEFAULT 0,
  `sub_pledge_id` INT NOT NULL DEFAULT '-2',
  PRIMARY KEY (`clan_id`,`skill_id`,`sub_pledge_id`),
  FOREIGN KEY (clan_id) REFERENCES clan_data(clan_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8MB4;