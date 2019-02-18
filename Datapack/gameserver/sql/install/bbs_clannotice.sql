DROP TABLE IF EXISTS `bbs_clannotice`;
CREATE TABLE `bbs_clannotice`
(
  `clan_id` INT NOT NULL,
  `type`    SMALLINT     NOT NULL DEFAULT '0',
  `notice`  text         NOT NULL,
  PRIMARY KEY (`clan_id`, `type`),
  FOREIGN KEY FK_BBSNOTICE_CLAN(clan_id) REFERENCES clan_data(clan_id) ON DELETE CASCADE
);
