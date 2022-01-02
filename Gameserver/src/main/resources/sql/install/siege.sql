DROP TABLE IF EXISTS `siege_mercenaries`;
DROP TABLE IF EXISTS siege_participants;
CREATE TABLE IF NOT EXISTS siege_participants
(
    `castle_id`         INT     NOT NULL,
    `clan_id`           INT  UNIQUE NOT NULL,
    `status`            ENUM ('ATTACKER', 'OWNER', 'WAITING', 'APPROVED', 'DECLINED'),
    `register_time`     DATETIME NOT NULL DEFAULT (CURRENT_TIMESTAMP),
    `recruit_mercenary` BOOLEAN NOT NULL DEFAULT FALSE,
    `mercenary_reward`  INT     NOT NULL DEFAULT 0,

    PRIMARY KEY (`clan_id`, `castle_id`),
    FOREIGN KEY (castle_id) REFERENCES castle (id) ON DELETE CASCADE,
    FOREIGN KEY (clan_id) REFERENCES clan_data (clan_id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS `siege_mercenaries`
(

    `clan_id`   INT          NOT NULL,
    `mercenary` INT UNSIGNED NOT NULL,
    `castle_id` INT          NOT NULL DEFAULT 0,

    PRIMARY KEY (`clan_id`, `mercenary`),
    FOREIGN KEY (`clan_id`) REFERENCES siege_participants (`clan_id`) ON DELETE CASCADE,
    FOREIGN KEY (`mercenary`) REFERENCES characters (charId) ON DELETE CASCADE,
    INDEX (`castle_id`)

) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

