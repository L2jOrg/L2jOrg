DROP TABLE IF EXISTS `castle_functions`;
CREATE TABLE IF NOT EXISTS `castle_functions` (
  `castle_id` INT NOT NULL,
  `type` int(1) NOT NULL DEFAULT '0',
  `level` int(3) NOT NULL DEFAULT '0',
  `lease` int(10) NOT NULL DEFAULT '0',
  `rate` decimal(20,0) NOT NULL DEFAULT '0',
  `endTime` bigint(13) unsigned NOT NULL DEFAULT '0',
  PRIMARY KEY (`castle_id`,`type`),
  FOREIGN KEY FK_CASTLE (`castle_id`) REFERENCES castle (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8MB4;