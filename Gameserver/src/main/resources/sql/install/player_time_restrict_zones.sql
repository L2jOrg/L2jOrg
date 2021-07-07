DROP TABLE IF EXISTS `player_time_restrict_zones`;
CREATE TABLE IF NOT EXISTS `player_time_restrict_zones` (
    `player_id` INT UNSIGNED NOT NULL,
    `zone` INT NOT NULL,
    `remaining_time` INT NOT NULL DEFAULT 0,
    `recharged_time` INT NOT NULL DEFAULT 0,
    PRIMARY KEY (`player_id`, `zone`),
    FOREIGN KEY (`player_id`) REFERENCES characters(`charId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

