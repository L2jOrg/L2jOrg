CREATE TABLE IF NOT EXISTS `accounts` (
  `login` varchar(64) NOT NULL,
  `password` varchar(255) NOT NULL,
  `last_access` BIGINT NOT NULL DEFAULT '0',
  `access_level` int(11) NOT NULL DEFAULT '0',
  `last_ip` varchar(15) DEFAULT NULL,
  `last_server` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`login`),
  KEY `last_ip` (`last_ip`)
) DEFAULT CHARSET=utf8;