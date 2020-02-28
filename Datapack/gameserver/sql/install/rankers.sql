CREATE TABLE IF NOT EXISTS `rankers_snapshot` (
    `char_id`   INT UNSIGNED     NOT NULL DEFAULT 0,
    `char_name` VARCHAR(35)      NOT NULL,
    `exp`       BIGINT UNSIGNED           DEFAULT 0,
    `class`     TINYINT UNSIGNED NOT NULL DEFAULT 0,
    `race`      TINYINT UNSIGNED          DEFAULT NULL,
    `clan_id`    INT UNSIGNED              DEFAULT NULL,
    `rank`      BIGINT UNSIGNED           DEFAULT 0,
    `rank_race` BIGINT UNSIGNED           DEFAULT 0
)ENGINE=InnoDB DEFAULT CHARSET=utf8MB4;;


CREATE TABLE IF NOT EXISTS `rankers_race_snapshot` (
    `char_id`   INT UNSIGNED     NOT NULL DEFAULT 0,
    `char_name` VARCHAR(35)      NOT NULL,
    `exp`       BIGINT UNSIGNED           DEFAULT 0,
    `class`     TINYINT UNSIGNED NOT NULL DEFAULT 0,
    `race`      TINYINT UNSIGNED          DEFAULT NULL,
    `clan_id`    INT UNSIGNED              DEFAULT NULL,
    `rank`      BIGINT UNSIGNED           DEFAULT 0
)ENGINE=InnoDB DEFAULT CHARSET=utf8MB4;