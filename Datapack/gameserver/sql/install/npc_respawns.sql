DROP TABLE IF EXISTS `npc_respawns`;
CREATE TABLE IF NOT EXISTS `npc_respawns` (
  `id` int(10) NOT NULL,
  `x` int(10) NOT NULL,
  `y` int(10) NOT NULL,
  `z` int(10) NOT NULL,
  `heading` int(10) NOT NULL,
  `respawnTime` bigint(20) unsigned NOT NULL DEFAULT '0',
  `currentHp` double unsigned NOT NULL,
  `currentMp` double unsigned NOT NULL,
  PRIMARY KEY (`id`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;