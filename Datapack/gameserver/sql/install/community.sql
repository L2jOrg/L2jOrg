DROP TABLE IF EXISTS `bbs_favorites`;
CREATE TABLE IF NOT EXISTS `bbs_favorites`
(
    `favId`      INT UNSIGNED NOT NULL AUTO_INCREMENT,
    `playerId`   INT UNSIGNED NOT NULL,
    `favTitle`   VARCHAR(50)  NOT NULL,
    `favBypass`  VARCHAR(127) NOT NULL,
    `favAddDate` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`favId`),
    UNIQUE INDEX `favId_playerId` (`favId`, `playerId`)
)
    COMMENT ='This table saves the Favorite links for the CB.'
    ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

DROP TABLE IF EXISTS `bbs_reports`;
CREATE TABLE IF NOT EXISTS `bbs_reports`
(
    `report_id`   INT          NOT NULL AUTO_INCREMENT,
    `player_id`   INT UNSIGNED NOT NULL,
    `report`      VARCHAR(500) NOT NULL,
    `report_date` TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `pending`     BOOL         NOT NULL DEFAULT TRUE,
    PRIMARY KEY (`report_id`),
    KEY (`report_date`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;

DROP TABLE IF EXISTS community_memos;
CREATE TABLE `community_memos`
(
    `id`         BIGINT              NOT NULL AUTO_INCREMENT,
    `owner_id`   INT UNSIGNED        NOT NULL,
    `date`       DATETIME            DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    `title`      VARCHAR(80)         NOT NULL DEFAULT '',
    `text`       VARCHAR(500)        NOT NULL,
    PRIMARY KEY (`id`),
    FOREIGN KEY (`owner_id`) REFERENCES characters (`charId`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;