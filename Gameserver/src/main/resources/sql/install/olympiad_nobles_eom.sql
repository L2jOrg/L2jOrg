DROP TABLE IF EXISTS `olympiad_nobles_eom`;
CREATE TABLE IF NOT EXISTS `olympiad_nobles_eom` (
  `charId` int(10) unsigned NOT NULL DEFAULT 0,
  `class_id` tinyint(3) unsigned NOT NULL DEFAULT 0,
  `olympiad_points` int(10) unsigned NOT NULL DEFAULT 0,
  `competitions_done` smallint(3) unsigned NOT NULL DEFAULT 0,
  `competitions_won` smallint(3) unsigned NOT NULL DEFAULT 0,
  `competitions_lost` smallint(3) unsigned NOT NULL DEFAULT 0,
  `competitions_drawn` smallint(3) unsigned NOT NULL DEFAULT 0,
  PRIMARY KEY (`charId`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;