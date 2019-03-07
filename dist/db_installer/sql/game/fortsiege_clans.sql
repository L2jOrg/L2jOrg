DROP TABLE IF EXISTS `fortsiege_clans`;
CREATE TABLE IF NOT EXISTS `fortsiege_clans` (
  `fort_id` int(1) NOT NULL DEFAULT '0',
  `clan_id` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`clan_id`,`fort_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;