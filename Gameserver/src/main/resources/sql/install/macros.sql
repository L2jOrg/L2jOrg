DROP TABLE IF EXISTS `macro_commands`;
DROP TABLE IF EXISTS `macros`;

CREATE TABLE IF NOT EXISTS `macros`
(
    `id`          INT          NOT NULL,
    `player_id`   INT UNSIGNED NOT NULL,
    `icon`        INT,
    `name`        VARCHAR(12),
    `description` VARCHAR(32),
    `acronym`     VARCHAR(4),
    PRIMARY KEY (`id`, `player_id`),
    FOREIGN KEY (`player_id`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS macro_commands
(
    `macro_id`        INT                                                              NOT NULL,
    `macro_player_id` INT UNSIGNED                                                     NOT NULL,
    `index`           INT                                                              NOT NULL,
    `type`            ENUM ('NONE', 'SKILL','ACTION','TEXT','SHORTCUT','ITEM','DELAY') NOT NULL DEFAULT 'NONE',
    `data1`           INT,
    `data2`           INT,
    `command`         VARCHAR(80),
    PRIMARY KEY (`macro_id`, `macro_player_id`, `index`),
    FOREIGN KEY (`macro_id`, `macro_player_id`) REFERENCES macros (`id`, `player_id`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;