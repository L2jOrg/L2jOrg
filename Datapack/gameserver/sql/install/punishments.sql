DROP TABLE IF EXISTS `punishments`;
CREATE TABLE IF NOT EXISTS `punishments` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `key` varchar(255) NOT NULL,
  `affect` varchar(255) NOT NULL,
  `type` varchar(255) NOT NULL,
  `expiration`  bigint NOT NULL,
  `reason` TEXT NOT NULL,
  `punishedBy` varchar(255) NOT NULL,
  PRIMARY KEY (`id`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;