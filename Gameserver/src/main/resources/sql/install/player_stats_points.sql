DROP TABLE IF EXISTS `player_stats_points`;
CREATE TABLE IF NOT EXISTS `player_stats_points` (
    `player_id` INT UNSIGNED NOT NULL,
    `points` SMALLINT NOT NULL DEFAULT 0,
    `strength` SMALLINT NOT NULL DEFAULT 0,
    `dexterity` SMALLINT NOT NULL DEFAULT 0,
    `constitution` SMALLINT NOT NULL DEFAULT 0,
    `intelligence` SMALLINT NOT NULL DEFAULT 0,
    `witness` SMALLINT NOT NULL DEFAULT 0,
    `mentality` SMALLINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`player_id`),
    FOREIGN KEY (`player_id`) REFERENCES characters(`charId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;