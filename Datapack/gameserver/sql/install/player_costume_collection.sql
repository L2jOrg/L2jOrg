DROP TABLE IF EXISTS `player_costume_collection`;
CREATE TABLE IF NOT EXISTS `player_costume_collection` (
    `player_id` INT UNSIGNED NOT NULL,
    `id` SMALLINT NOT NULL DEFAULT 0,
    `reuse` BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`player_id`),
    FOREIGN KEY (`player_id`) REFERENCES characters(`charId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;