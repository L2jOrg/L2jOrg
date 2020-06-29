DROP TABLE IF EXISTS `event_schedulers`;
CREATE TABLE IF NOT EXISTS `event_schedulers` (
  `id` INT unsigned NOT NULL AUTO_INCREMENT,
  `eventName` VARCHAR(255) NOT NULL,
  `schedulerName` VARCHAR(255) NOT NULL,
  `lastRun` BIGINT NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `eventName_schedulerName` (`eventName`,`schedulerName`) USING BTREE
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;