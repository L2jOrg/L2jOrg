DROP TABLE IF EXISTS lcoin_shop_history;
CREATE TABLE lcoin_shop_history (
    `product_id` INT  NOT NULL,
    `count`      INT  NOT NULL DEFAULT '1',
    `account`    VARCHAR(45) NOT NULL,
    `sell_date`  DATE NOT NULL DEFAULT (CURRENT_DATE),
    `restriction_type` ENUM('DAY', 'MONTH', 'EVER') NOT NULL DEFAULT 'DAY',
    PRIMARY KEY productId (`product_id`, `account`, `sell_date`),
    FOREIGN KEY (`account`) REFERENCES account_data (`account`) ON DELETE CASCADE,
    INDEX IDX_COIN_SHOP_RESTRICTION(restriction_type)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;