DROP TABLE IF EXISTS `event_schedulers`;
CREATE TABLE IF NOT EXISTS `event_schedulers` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `eventName` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `schedulerName` varchar(255) COLLATE utf8_unicode_ci NOT NULL,
  `lastRun` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`),
  UNIQUE KEY `eventName_schedulerName` (`eventName`,`schedulerName`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_unicode_ci;