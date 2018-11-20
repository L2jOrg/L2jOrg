CREATE TABLE IF NOT EXISTS  `hardware_limits` (
  `hardware` varchar(255) NOT NULL,
  `windows_limit` smallint(5) NOT NULL,
  `limit_expire` int(11) NOT NULL,
  PRIMARY KEY (`hardware`)
);