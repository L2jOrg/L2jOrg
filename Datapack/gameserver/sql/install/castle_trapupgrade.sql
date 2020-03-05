DROP TABLE IF EXISTS castle_trap_upgrade;
CREATE TABLE IF NOT EXISTS castle_trap_upgrade (
  `castle_id` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `tower_index` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `level` tinyint(3) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`castle_id`, `tower_index`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8MB4;