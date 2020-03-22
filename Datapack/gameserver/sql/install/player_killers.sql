DROP TABLE IF EXISTS `player_killers`;
CREATE TABLE IF NOT EXISTS `player_killers` (
   `player_id` INT UNSIGNED NOT NULL,
   `killer_id` INT UNSIGNED NOT NULL,
   `kill_time` BIGINT NOT NULL,
   PRIMARY KEY (`player_id`, `killer_id`),
   FOREIGN KEY (`player_id`) REFERENCES characters (`charId`) ON DELETE CASCADE,
   FOREIGN KEY (`killer_id`) REFERENCES characters(`charId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;