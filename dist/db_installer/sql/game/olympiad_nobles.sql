DROP TABLE IF EXISTS `olympiad_nobles`;
CREATE TABLE IF NOT EXISTS `olympiad_nobles` (
  `charId` int(10) unsigned NOT NULL DEFAULT 0,
  `class_id` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `olympiad_points` int(10) unsigned NOT NULL DEFAULT 0,
  `competitions_done` smallint(3) unsigned NOT NULL DEFAULT 0,
  `competitions_won` smallint(3) unsigned NOT NULL DEFAULT 0,
  `competitions_lost` smallint(3) unsigned NOT NULL DEFAULT 0,
  `competitions_drawn` smallint(3) unsigned NOT NULL DEFAULT 0,
  `competitions_done_week` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `competitions_done_week_classed` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `competitions_done_week_non_classed` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `competitions_done_week_team` tinyint(3) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`charId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;