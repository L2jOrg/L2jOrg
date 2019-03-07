DROP TABLE IF EXISTS `clan_wars`;
CREATE TABLE IF NOT EXISTS `clan_wars` (
  `clan1` varchar(35) NOT NULL DEFAULT '',
  `clan2` varchar(35) NOT NULL DEFAULT '',
  `clan1Kill` int(11) NOT NULL DEFAULT 0,
  `clan2Kill` int(11) NOT NULL DEFAULT 0,
  `winnerClan` varchar(35) NOT NULL DEFAULT '0',
  `startTime` bigint(13) NOT NULL DEFAULT 0,
  `endTime` bigint(13) NOT NULL DEFAULT 0,
  `state` tinyint(4) NOT NULL DEFAULT 0,
  KEY `clan1` (`clan1`),
  KEY `clan2` (`clan2`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;