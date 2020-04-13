DROP TABLE IF EXISTS `player_variables`;
CREATE TABLE IF NOT EXISTS `player_variables` (
    `player_id` INT UNSIGNED NOT NULL,
    `revenge_teleports` SMALLINT NOT NULL DEFAULT 0,
    `revenge_locations` SMALLINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`player_id`),
    FOREIGN KEY (`player_id`) REFERENCES characters(`charId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;