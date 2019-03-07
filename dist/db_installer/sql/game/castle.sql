DROP TABLE IF EXISTS `castle`;
CREATE TABLE IF NOT EXISTS `castle` (
  `id` INT NOT NULL DEFAULT 0,
  `name` varchar(25) NOT NULL,
  `side` enum('NEUTRAL','LIGHT','DARK') DEFAULT 'NEUTRAL' NOT NULL,
  `treasury` BIGINT NOT NULL DEFAULT 0,
  `siegeDate` bigint(13) unsigned NOT NULL DEFAULT '0',
  `regTimeOver` enum('true','false') DEFAULT 'true' NOT NULL,
  `regTimeEnd` bigint(13) unsigned NOT NULL DEFAULT '0',
  `showNpcCrest` enum('true','false') DEFAULT 'false' NOT NULL,
  `ticketBuyCount` smallint(3) NOT NULL DEFAULT 0,
  PRIMARY KEY (`name`),
  KEY `id` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT IGNORE INTO `castle` VALUES
(1,'Gludio','NEUTRAL',0,0,'true',0,'false',0),
(2,'Dion','NEUTRAL',0,0,'true',0,'false',0),
(3,'Giran','NEUTRAL',0,0,'true',0,'false',0),
(4,'Oren','NEUTRAL',0,0,'true',0,'false',0),
(5,'Aden','NEUTRAL',0,0,'true',0,'false',0);
-- (6,'Innadril','NEUTRAL',0,0,'true',0,'false',0),
-- (7,'Goddard','NEUTRAL',0,0,'true',0,'false',0),
-- (8,'Rune','NEUTRAL',0,0,'true',0,'false',0),
-- (9,'Schuttgart','NEUTRAL',0,0,'true',0,'false',0);