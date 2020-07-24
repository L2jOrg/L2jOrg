DROP TABLE IF EXISTS `player_teleports`;
CREATE TABLE IF NOT EXISTS `player_teleports` (
    `player_id` INT UNSIGNED NOT NULL,
    `teleport_id` SMALLINT NOT NULL DEFAULT 0,
    PRIMARY KEY (`player_id`, `teleport_id`),
    FOREIGN KEY (`player_id`) REFERENCES characters(`charId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;