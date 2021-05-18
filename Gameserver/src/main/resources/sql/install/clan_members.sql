DROP TABLE IF EXISTS `clan_members`;
CREATE TABLE IF NOT EXISTS `clan_members`
(
    `clan_id`               INT          NOT NULL,
    `player_id`             INT UNSIGNED NOT NULL,
    `last_reputation_level` SMALLINT     NOT NULL DEFAULT 0,

    PRIMARY KEY (`clan_id`, `player_id`),
    FOREIGN KEY (`clan_id`) REFERENCES clan_data (clan_id) ON DELETE CASCADE,
    FOREIGN KEY (`player_id`) REFERENCES characters (charId) ON DELETE CASCADE
)
