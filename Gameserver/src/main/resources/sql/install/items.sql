DROP TABLE IF EXISTS `items`;
CREATE TABLE IF NOT EXISTS `items`
(
    `owner_id`      INT,                                -- object id of the player or clan,owner of this item
    `object_id`     INT             NOT NULL DEFAULT 0, -- object id of the item
    `item_id`       INT,
    `count`         BIGINT UNSIGNED NOT NULL DEFAULT 0,
    `enchant_level` INT,
    `loc`           VARCHAR(10),                        -- inventory,paperdoll,npc,clan warehouse,pet,and so on
    `loc_data`      INT,                                -- depending on location: equiped slot,npc id,pet id,etc
    `time_of_use`   INT,                                -- time of item use, for calculate of breackages
    `time`          decimal(13)     NOT NULL DEFAULT 0,
    PRIMARY KEY (`object_id`),
    KEY `owner_id` (`owner_id`),
    KEY `item_id` (`item_id`),
    KEY `loc` (`loc`),
    KEY `time_of_use` (`time_of_use`)
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

DROP TABLE IF EXISTS `item_variations`;
CREATE TABLE IF NOT EXISTS `item_variations`
(
    `itemId`    INT NOT NULL,
    `mineralId` INT NOT NULL DEFAULT 0,
    `option1`   INT NOT NULL,
    `option2`   INT NOT NULL,
    PRIMARY KEY (`itemId`),
    FOREIGN KEY (`itemId`) REFERENCES items(object_id) ON DELETE CASCADE
) ENGINE = InnoDB
  DEFAULT CHARSET = UTF8MB4;

DROP TABLE IF EXISTS `item_special_abilities`;
CREATE TABLE IF NOT EXISTS `item_special_abilities` (
    `objectId` INT NOT NULL,
    `type` TINYINT unsigned NOT NULL DEFAULT 1,
    `optionId` INT unsigned NOT NULL,
    `position` TINYINT unsigned NOT NULL DEFAULT 0,
    PRIMARY KEY (`objectId`,`optionId`),
    FOREIGN KEY (`objectId`) REFERENCES items(`object_id`) ON DELETE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;