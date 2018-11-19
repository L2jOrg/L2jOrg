CREATE TABLE IF NOT EXISTS `accounts` (
  `login` varchar(32) NOT NULL,
  `password` varchar(255) NOT NULL,
  `last_access` int(11) NOT NULL DEFAULT '0',
  `access_level` int(11) NOT NULL DEFAULT '0',
  `last_ip` varchar(15) DEFAULT NULL,
  `last_server` int(11) NOT NULL DEFAULT '0',
  `bonus` int(11) NOT NULL DEFAULT '0',
  `bonus_expire` int(11) NOT NULL DEFAULT '0',
  `ban_expire` int(11) NOT NULL DEFAULT '0',
  `allow_ip` varchar(255) NOT NULL DEFAULT '',
  `allow_hwid` varchar(255) NOT NULL DEFAULT '',
  `points` int(11) NOT NULL DEFAULT '0',
  `phone_nubmer` bigint(15) NOT NULL DEFAULT '0',
  PRIMARY KEY (`login`),
  KEY `last_ip` (`last_ip`)
) DEFAULT CHARSET=utf8;