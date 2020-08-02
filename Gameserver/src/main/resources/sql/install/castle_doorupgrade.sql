DROP TABLE IF EXISTS `castle_doorupgrade`;
CREATE TABLE IF NOT EXISTS `castle_doorupgrade` (
  `doorId` int(8) unsigned NOT NULL DEFAULT '0',
  `ratio` tinyint(3) unsigned NOT NULL DEFAULT '0',
  `castleId` tinyint(3) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`doorId`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;