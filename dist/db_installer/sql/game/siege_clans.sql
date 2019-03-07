DROP TABLE IF EXISTS `siege_clans`;
CREATE TABLE IF NOT EXISTS `siege_clans` (
   `castle_id` int(1) NOT NULL DEFAULT 0,
   `clan_id` int(11) NOT NULL DEFAULT 0,
   `type` int(1) DEFAULT NULL,
   `castle_owner` int(1) DEFAULT NULL,
   PRIMARY KEY (`clan_id`,`castle_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;