DROP TABLE IF EXISTS `clan_wars`;
CREATE TABLE IF NOT EXISTS `clan_wars` (
  `clan1` INT NOT NULL,
  `clan2` INT NOT NULL,
  `clan1Kill` int(11) NOT NULL DEFAULT 0,
  `clan2Kill` int(11) NOT NULL DEFAULT 0,
  `winnerClan` varchar(35) NOT NULL DEFAULT '0',
  `startTime` bigint(13) NOT NULL DEFAULT 0,
  `endTime` bigint(13) NOT NULL DEFAULT 0,
  `state` tinyint(4) NOT NULL DEFAULT 0,
  FOREIGN KEY (`clan1`) REFERENCES clan_data(clan_id) ON DELETE CASCADE,
  FOREIGN KEY (`clan2`) REFERENCES clan_data(clan_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8MB4;