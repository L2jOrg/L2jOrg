CREATE TABLE IF NOT EXISTS `account_log` (
  `time` int(11) NOT NULL,
  `login` varchar(32) NOT NULL,
  `ip` varchar(15) NOT NULL,
  KEY `login` (`login`),
  KEY `ip` (`ip`)
) DEFAULT CHARSET=utf8