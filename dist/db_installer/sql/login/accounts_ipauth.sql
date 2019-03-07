DROP TABLE IF EXISTS `accounts_ipauth`;
CREATE TABLE IF NOT EXISTS `accounts_ipauth` (
  `login` varchar(45) NOT NULL,
  `ip` char(15) NOT NULL,
  `type` enum('deny','allow') NULL DEFAULT 'allow'
) ENGINE=InnoDB DEFAULT CHARSET=utf8;