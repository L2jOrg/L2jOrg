DROP TABLE IF EXISTS `olympiad_data`;
CREATE TABLE IF NOT EXISTS `olympiad_data` (
  `id` TINYINT UNSIGNED NOT NULL DEFAULT 0,
  `current_cycle` MEDIUMINT UNSIGNED NOT NULL DEFAULT 1,
  `period` MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,
  `olympiad_end` bigint(13) unsigned NOT NULL DEFAULT '0',
  `validation_end` bigint(13) unsigned NOT NULL DEFAULT '0',
  `next_weekly_change` bigint(13) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`id`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;