CREATE TABLE IF NOT EXISTS `clan_subpledges_skills` (
  `clan_id` int(11) NOT NULL DEFAULT '0',
  `type` int(11) NOT NULL,
  `skill_id` smallint(5) unsigned NOT NULL DEFAULT '0',
  `skill_level` tinyint(3) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`clan_id`,`type`,`skill_id`),
  FOREIGN KEY FK_SUBPLEDGE_SKILL_CLAN(clan_id) REFERENCES clan_data(clan_id) ON DELETE CASCADE
);