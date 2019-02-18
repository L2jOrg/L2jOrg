DROP TABLE IF EXISTS `pvp_system_log`;
CREATE TABLE `pvp_system_log` (
  `killer` varchar(255) NOT NULL,
  `victim` varchar(255) NOT NULL
);
