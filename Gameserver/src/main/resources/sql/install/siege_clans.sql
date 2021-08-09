DROP TABLE IF EXISTS `siege_clans`;
CREATE TABLE IF NOT EXISTS `siege_clans` (
   `castle_id` int(1) NOT NULL DEFAULT 0,
   `clan_id` int(11) NOT NULL DEFAULT 0,
   `type` ENUM('OWNER', 'DEFENDER_PENDING', 'DEFENDER', 'ATTACKER') DEFAULT NULL,
   PRIMARY KEY (`clan_id`,`castle_id`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;
