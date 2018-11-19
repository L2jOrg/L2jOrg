DROP TABLE IF EXISTS `clan_wars`;
CREATE TABLE `clan_wars` (
  `attacker_clan` int(11) NOT NULL,
  `opposing_clan` int(11) NOT NULL,
  `period` enum('PREPARATION','MUTUAL','PEACE') NOT NULL DEFAULT 'PREPARATION',
  `period_start_time` int(11) NOT NULL DEFAULT '0',
  `last_kill_time` int(11) NOT NULL DEFAULT '0',
  `attackers_kill_counter` int(11) NOT NULL DEFAULT '0',
  `opposers_kill_counter` int(11) NOT NULL DEFAULT '0',
  PRIMARY KEY (`attacker_clan`,`opposing_clan`),
  UNIQUE KEY `attacker_clan` (`attacker_clan`),
  UNIQUE KEY `opposing_clan` (`opposing_clan`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;