DROP TABLE IF EXISTS `pledge_waiting_list`;
DROP TABLE IF EXISTS `rankers_snapshot`;
DROP TABLE IF EXISTS `bot_reported_char_data`;
DROP TABLE IF EXISTS `character_contacts`;
DROP TABLE IF EXISTS `character_hennas`;
DROP TABLE IF EXISTS `character_instance_time`;
DROP TABLE IF EXISTS `character_missions`;
DROP TABLE IF EXISTS `character_quests`;
DROP TABLE IF EXISTS `character_recipeshoplist`;
DROP TABLE IF EXISTS `character_reco_bonus`;
DROP TABLE IF EXISTS `character_relationship`;
DROP TABLE IF EXISTS `character_shortcuts`;
DROP TABLE IF EXISTS `character_skills`;
DROP TABLE IF EXISTS `character_skills_save`;
DROP TABLE IF EXISTS `character_spirits`;
DROP TABLE IF EXISTS `community_memos`;
DROP TABLE IF EXISTS `macro_commands`;
DROP TABLE IF EXISTS `macros`;
DROP TABLE IF EXISTS `olympiad_participants`;
DROP TABLE IF EXISTS `olympiad_matches`;
DROP TABLE IF EXISTS olympiad_history;
DROP TABLE IF EXISTS `olympiad_heroes_matches`;
DROP TABLE IF EXISTS olympiad_heroes_history;
DROP TABLE IF EXISTS olympiad_heroes;
DROP TABLE IF EXISTS `player_costumes`;
DROP TABLE IF EXISTS `player_costume_collection`;
DROP TABLE IF EXISTS `player_killers`;
DROP TABLE IF EXISTS `player_stats_points`;
DROP TABLE IF EXISTS `player_teleports`;
DROP TABLE IF EXISTS `player_time_restrict_zones`;
DROP TABLE IF EXISTS `player_variables`;
DROP TABLE IF EXISTS `recipes`;
DROP TABLE IF EXISTS `characters`;

CREATE TABLE IF NOT EXISTS `characters`
(
    `charId`                  INT UNSIGNED       NOT NULL,
    `account_name`            VARCHAR(45)                 DEFAULT NULL,
    `char_name`               VARCHAR(35)        NOT NULL,
    `level`                   TINYINT UNSIGNED            DEFAULT NULL,
    `maxHp`                   MEDIUMINT UNSIGNED          DEFAULT NULL,
    `curHp`                   MEDIUMINT UNSIGNED          DEFAULT NULL,
    `maxCp`                   MEDIUMINT UNSIGNED          DEFAULT NULL,
    `curCp`                   MEDIUMINT UNSIGNED          DEFAULT NULL,
    `maxMp`                   MEDIUMINT UNSIGNED          DEFAULT NULL,
    `curMp`                   MEDIUMINT UNSIGNED          DEFAULT NULL,
    `face`                    TINYINT UNSIGNED            DEFAULT NULL,
    `hairStyle`               TINYINT UNSIGNED            DEFAULT NULL,
    `hairColor`               TINYINT UNSIGNED            DEFAULT NULL,
    `sex`                     TINYINT UNSIGNED            DEFAULT NULL,
    `heading`                 MEDIUMINT                   DEFAULT NULL,
    `x`                       MEDIUMINT                   DEFAULT NULL,
    `y`                       MEDIUMINT                   DEFAULT NULL,
    `z`                       MEDIUMINT                   DEFAULT NULL,
    `exp`                     BIGINT UNSIGNED             DEFAULT 0,
    `expBeforeDeath`          BIGINT UNSIGNED             DEFAULT 0,
    `sp`                      BIGINT UNSIGNED    NOT NULL DEFAULT 0,
    `reputation`              INT                         DEFAULT NULL,
    `fame`                    MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,
    `raidbossPoints`          MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,
    `pvpkills`                SMALLINT UNSIGNED           DEFAULT NULL,
    `pkkills`                 SMALLINT UNSIGNED           DEFAULT NULL,
    `clanid`                  INT UNSIGNED                DEFAULT NULL,
    `race`                    TINYINT UNSIGNED            DEFAULT NULL,
    `classid`                 TINYINT UNSIGNED            DEFAULT NULL,
    `base_class`              TINYINT UNSIGNED   NOT NULL DEFAULT 0,
    `transform_id`            SMALLINT UNSIGNED  NOT NULL DEFAULT 0,
    `deletetime`              BIGINT UNSIGNED    NOT NULL DEFAULT '0',
    `cancraft`                TINYINT UNSIGNED            DEFAULT NULL,
    `title`                   VARCHAR(21)                 DEFAULT NULL,
    `title_color`             MEDIUMINT UNSIGNED NOT NULL DEFAULT 0xECF9A2,
    `accesslevel`             MEDIUMINT                   DEFAULT 0,
    `online`                  TINYINT UNSIGNED            DEFAULT NULL,
    `onlinetime`              INT                         DEFAULT NULL,
    `char_slot`               TINYINT UNSIGNED            DEFAULT NULL,
    `lastAccess`              BIGINT UNSIGNED    NOT NULL DEFAULT '0',
    `clan_privs`              INT UNSIGNED                DEFAULT 0,
    `wantspeace`              TINYINT UNSIGNED            DEFAULT 0,
    `power_grade`             TINYINT UNSIGNED            DEFAULT NULL,
    `apprentice`              INT UNSIGNED       NOT NULL DEFAULT 0,
    `sponsor`                 INT UNSIGNED       NOT NULL DEFAULT 0,
    `clan_join_expiry_time`   BIGINT UNSIGNED    NOT NULL DEFAULT '0',
    `clan_create_expiry_time` BIGINT UNSIGNED    NOT NULL DEFAULT '0',
    `bookmarkslot`            SMALLINT UNSIGNED  NOT NULL DEFAULT 0,
    `vitality_points`         MEDIUMINT UNSIGNED NOT NULL DEFAULT 0,
    `createDate`              DATE               NOT NULL DEFAULT (CURRENT_DATE),
    `pccafe_points`           INT                NOT NULL DEFAULT '0',
    PRIMARY KEY (`charId`),
    KEY `account_name` (`account_name`),
    KEY `char_name` (`char_name`),
    KEY `clanid` (`clanid`),
    KEY `online` (`online`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8MB4;

CREATE TABLE IF NOT EXISTS `clan_members`
(
    `clan_id`               INT          NOT NULL,
    `player_id`             INT UNSIGNED NOT NULL,
    `last_reputation_level` SMALLINT     NOT NULL DEFAULT 0,

    PRIMARY KEY (`clan_id`, `player_id`),
    FOREIGN KEY (`clan_id`) REFERENCES clan_data (clan_id) ON DELETE CASCADE,
    FOREIGN KEY (`player_id`) REFERENCES characters (charId) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS `pledge_waiting_list`
(
    `char_id` INT UNSIGNED NOT NULL,
    `karma`   tinyint(1)   NOT NULL,
    PRIMARY KEY (`char_id`),
    FOREIGN KEY (`char_id`) REFERENCES characters (charId) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS `bot_reported_char_data`
(
    `bot_id`      INT UNSIGNED              NOT NULL DEFAULT 0,
    `reporter_id` INT UNSIGNED              NOT NULL DEFAULT 0,
    `type`        ENUM ('BOT', 'ADENA_ADS') NOT NULL DEFAULT 'BOT',
    `report_date` TIMESTAMP                 NOT NULL DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (`bot_id`, `reporter_id`, `type`),
    FOREIGN KEY FK_BOT_CHARACTER (`bot_id`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS `character_contacts`
(
    charId    INT UNSIGNED NOT NULL DEFAULT 0,
    contactId INT UNSIGNED NOT NULL DEFAULT 0,

    PRIMARY KEY (`charId`, `contactId`),
    FOREIGN KEY FK_CONTANCTS_CHARACTER (`charId`) REFERENCES characters (`charId`) ON DELETE CASCADE,
    FOREIGN KEY FK_CONTANCTS_CONTACT (`charId`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS `character_hennas`
(
    `charId`    INT UNSIGNED NOT NULL DEFAULT 0,
    `symbol_id` INT,
    `slot`      INT          NOT NULL DEFAULT 0,
    PRIMARY KEY (`charId`, `slot`),
    FOREIGN KEY FK_CHARACTER_HENNA (`charId`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS `character_instance_time`
(
    `charId`     INT UNSIGNED        NOT NULL DEFAULT '0',
    `instanceId` int(3)              NOT NULL DEFAULT '0',
    `time`       bigint(13) unsigned NOT NULL DEFAULT '0',

    PRIMARY KEY (`charId`, `instanceId`),
    FOREIGN KEY FK_CHARACTER_INSTANCE (`charId`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;


CREATE TABLE IF NOT EXISTS character_missions
(
    `char_id`    INT UNSIGNED                                     NOT NULL,
    `mission_id` INT UNSIGNED                                     NOT NULL,
    `status`     ENUM ('AVAILABLE', 'NOT_AVAILABLE', 'COMPLETED') NOT NULL DEFAULT 'NOT_AVAILABLE',
    `progress`   INT UNSIGNED                                     NOT NULL DEFAULT 0,
    PRIMARY KEY (`char_id`, `mission_id`),
    INDEX IDX_MISSION (`mission_id`),
    FOREIGN KEY FK_MISSION_CHARACTER (`char_id`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS `character_quests`
(
    `charId` INT UNSIGNED NOT NULL DEFAULT 0,
    `name`   VARCHAR(60)  NOT NULL DEFAULT '',
    `var`    VARCHAR(20)  NOT NULL DEFAULT '',
    `value`  VARCHAR(255),

    PRIMARY KEY (`charId`, `name`, `var`),
    FOREIGN KEY FK_CHARACTER_QUEST (`charId`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS `character_recipeshoplist`
(
    `charId`   int(10) unsigned    NOT NULL DEFAULT 0,
    `recipeId` int(11) UNSIGNED    NOT NULL DEFAULT 0,
    `price`    bigint(20) UNSIGNED NOT NULL DEFAULT 0,
    `index`    tinyint(3) UNSIGNED NOT NULL DEFAULT 0,

    PRIMARY KEY (`charId`, `recipeId`),
    FOREIGN KEY FK_CHARACTER_RECIPE_SHOP_LIST (`charId`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS `character_reco_bonus`
(
    `charId`    INT unsigned        NOT NULL,
    `rec_have`  tinyint(3) unsigned NOT NULL DEFAULT '0',
    `rec_left`  tinyint(3) unsigned NOT NULL DEFAULT '0',
    `time_left` bigint(13) unsigned NOT NULL DEFAULT '0',
    PRIMARY KEY `charId` (`charId`),
    FOREIGN KEY (`charId`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS character_relationship
(
    `char_id`   INT UNSIGNED             NOT NULL DEFAULT 0,
    `friend_id` INT UNSIGNED             NOT NULL DEFAULT 0,
    `relation`  ENUM ('FRIEND', 'BLOCK') NOT NULL DEFAULT 'FRIEND',

    PRIMARY KEY (`char_id`, `friend_id`),
    KEY `relation` (`relation`),
    FOREIGN KEY FK_FRIENDS_CHARACTER (`char_id`) REFERENCES characters (`charId`) ON DELETE CASCADE,
    FOREIGN KEY FK_FRIENDS_FRIEND (`friend_id`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE `character_shortcuts`
(
    `player_id`      INT UNSIGNED                                                    NOT NULL DEFAULT 0,
    `client_id`      INT                                                             NOT NULL DEFAULT 0,
    `type`           ENUM ('ITEM', 'SKILL', 'ACTION', 'MACRO', 'RECIPE', 'BOOKMARK') NOT NULL,
    `shortcut_id`    INT                                                             NOT NULL,
    `level`          SMALLINT,
    `sub_level`      INT                                                             NOT NULL DEFAULT 0,
    `character_type` SMALLINT                                                        NOT NULL DEFAULT 1,
    `active`         BOOLEAN                                                         NOT NULL DEFAULT false,

    PRIMARY KEY (`player_id`, `client_id`),
    FOREIGN KEY FK_SHORTCUTS_CHARACTERS (`player_id`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS `character_skills`
(
    `charId`          INT UNSIGNED NOT NULL DEFAULT 0,
    `skill_id`        INT          NOT NULL DEFAULT 0,
    `skill_level`     INT          NOT NULL DEFAULT 1,
    `skill_sub_level` INT          NOT NULL DEFAULT '0',
    PRIMARY KEY (`charId`, `skill_id`),
    FOREIGN KEY FK_CHARACTER_SKILL (`charId`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS `character_skills_save`
(
    `charId`          INT UNSIGNED    NOT NULL DEFAULT 0,
    `skill_id`        INT             NOT NULL DEFAULT 0,
    `skill_level`     INT             NOT NULL DEFAULT 1,
    `skill_sub_level` INT             NOT NULL DEFAULT '0',
    `remaining_time`  INT             NOT NULL DEFAULT 0,
    `reuse_delay`     INT             NOT NULL DEFAULT 0,
    `systime`         BIGINT UNSIGNED NOT NULL DEFAULT '0',
    `restore_type`    INT             NOT NULL DEFAULT 0,
    `buff_index`      INT             NOT NULL DEFAULT 0,

    PRIMARY KEY (`charId`, `skill_id`, `skill_level`),
    FOREIGN KEY FK_CHARACTER_SKILL_SAVE (`charId`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE `character_spirits`
(
    `charId`             INT UNSIGNED NOT NULL,
    `type`               TINYINT      NOT NULL,
    `level`              TINYINT      NOT NULL DEFAULT 1,
    `stage`              TINYINT      NOT NULL DEFAULT 0,
    `experience`         BIGINT       NOT NULL DEFAULT 0,
    `attack_points`      TINYINT      NOT NULL DEFAULT 0,
    `defense_points`     TINYINT      NOT NULL DEFAULT 0,
    `crit_rate_points`   TINYINT      NOT NULL DEFAULT 0,
    `crit_damage_points` TINYINT      NOT NULL DEFAULT 0,
    `in_use`             BOOLEAN      NOT NULL DEFAULT FALSE,
    PRIMARY KEY (`charId`, `type`),
    FOREIGN KEY FK_CHARACTER_SPIRITS (`charId`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS `player_killers`
(
    `player_id` INT UNSIGNED NOT NULL,
    `killer_id` INT UNSIGNED NOT NULL,
    `kill_time` BIGINT       NOT NULL,
    PRIMARY KEY (`player_id`, `killer_id`),
    FOREIGN KEY (`player_id`) REFERENCES characters (`charId`) ON DELETE CASCADE,
    FOREIGN KEY (`killer_id`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS `player_stats_points`
(
    `player_id`    INT UNSIGNED NOT NULL,
    `points`       SMALLINT     NOT NULL DEFAULT 0,
    `strength`     SMALLINT     NOT NULL DEFAULT 0,
    `dexterity`    SMALLINT     NOT NULL DEFAULT 0,
    `constitution` SMALLINT     NOT NULL DEFAULT 0,
    `intelligence` SMALLINT     NOT NULL DEFAULT 0,
    `witness`      SMALLINT     NOT NULL DEFAULT 0,
    `mentality`    SMALLINT     NOT NULL DEFAULT 0,
    PRIMARY KEY (`player_id`),
    FOREIGN KEY (`player_id`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS `player_teleports`
(
    `player_id`   INT UNSIGNED NOT NULL,
    `teleport_id` SMALLINT     NOT NULL DEFAULT 0,
    PRIMARY KEY (`player_id`, `teleport_id`),
    FOREIGN KEY (`player_id`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS `player_time_restrict_zones`
(
    `player_id`      INT UNSIGNED             NOT NULL,
    `zone`           INT                      NOT NULL,
    `remaining_time` INT                      NOT NULL DEFAULT 0,
    `recharged_time` INT                      NOT NULL DEFAULT 0,
    `reset_cycle`    ENUM ('DAILY', 'WEEKLY') NOT NULL DEFAULT 'DAILY',
    PRIMARY KEY (`player_id`, `zone`),
    FOREIGN KEY (`player_id`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

CREATE TABLE IF NOT EXISTS `recipes`
(
    `player_id` INT UNSIGNED NOT NULL,
    `id`        INT          NOT NULL,
    PRIMARY KEY (`player_id`, `id`),
    FOREIGN KEY (`player_id`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;


CREATE TABLE IF NOT EXISTS `rankers_snapshot`
(
    `id`        INT UNSIGNED NOT NULL DEFAULT 0,
    `exp`       BIGINT UNSIGNED       DEFAULT 0,
    `rank`      BIGINT UNSIGNED       DEFAULT 0,
    `rank_race` BIGINT UNSIGNED       DEFAULT 0,
    PRIMARY KEY (`id`),
    FOREIGN KEY FK_RANKERS_SNAPSHOT (`id`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8MB4;

CREATE OR REPLACE VIEW rankers_race AS
WITH ranked_race AS (
    SELECT c.charId                as id,
           c.char_name             as name,
           c.exp,
           c.level,
           c.base_class            as class,
           c.race,

           IFNULL((SELECT clan_name
                   FROM clan_data clan
                   WHERE clan.clan_id = c.clanid), ''
               )                   as clan_name,

           rank() over (
               PARTITION BY c.race
               ORDER BY c.exp desc, c.onlinetime desc
               )                   as `rank`,

           IFNULL(rs.`rank`, 0)    as `rank_snapshot`,
           IFNULL(rs.rank_race, 0) as `rank_race_snapshot`

    from characters c
             LEFT JOIN rankers_snapshot rs on c.charId = rs.id
    where c.level >= 76
      AND c.accesslevel = 0
      AND (c.base_class BETWEEN 88 AND 118 OR c.base_class IN (131, 134, 195))
)
SELECT *
FROM ranked_race
WHERE `rank` <= 100;

CREATE OR REPLACE VIEW rankers AS
SELECT c.charId                as id,
       c.char_name             as name,
       c.exp,
       c.level,
       c.base_class            as class,
       c.race,
       c.clanid                as clan_id,
       IFNULL((SELECT clan_name
               FROM clan_data clan
               WHERE clan.clan_id = c.clanid), ''
           )                   as clan_name,

       rank() over (w)         as `rank`,

       (SELECT `rank`
        FROM rankers_race r
        WHERE r.id = c.charId
       )                       as `rank_race`,

       IFNULL(rs.`rank`, 0)    as `rank_snapshot`,
       IFNULL(rs.rank_race, 0) as `rank_race_snapshot`

FROM characters c
         LEFT JOIN rankers_snapshot rs on c.charId = rs.id
WHERE c.level >= 76
  AND c.accesslevel = 0
  AND (c.base_class BETWEEN 88 AND 118 OR c.base_class IN (131, 134, 195))
    WINDOW w as (ORDER BY c.exp desc, onlinetime desc );

CREATE TABLE IF NOT EXISTS `rankers_history`
(
    `id`   INT UNSIGNED NOT NULL,
    `exp`  BIGINT UNSIGNED DEFAULT 0,
    `rank` BIGINT UNSIGNED DEFAULT 0,
    `date` INT UNSIGNED,
    PRIMARY KEY (`id`, `date`)
) ENGINE = InnoDB
  DEFAULT CHARSET = utf8MB4;