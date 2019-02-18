DROP TABLE IF EXISTS `siege_players`;
CREATE TABLE `siege_players` (
  `residence_id` int(11) NOT NULL,
  `object_id` int(11) NOT NULL,
  `clan_id` int(11) NOT NULL,
  PRIMARY KEY (`residence_id`,`object_id`,`clan_id`),
  FOREIGN KEY FK_SIEGE_CHARACTER(object_id) REFERENCES characters(obj_Id) ON DELETE CASCADE,
  FOREIGN KEY FK_SIEGE_PLAYER_CLAN(clan_id) REFERENCES clan_data(clan_id) ON DELETE CASCADE
);
