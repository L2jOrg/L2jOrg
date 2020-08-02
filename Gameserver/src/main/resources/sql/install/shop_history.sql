DROP TABLE IF EXISTS shop_history;
CREATE TABLE shop_history (
    `product_id` INT  NOT NULL,
    `count`      INT  NOT NULL DEFAULT '1',
    `bidder`     INT UNSIGNED NOT NULL,
    `sell_date`  DATE NOT NULL DEFAULT (CURRENT_DATE),
    KEY productId (`product_id`, `bidder`, `sell_date`),
    FOREIGN KEY SHOP_BIDDER (`bidder`) REFERENCES characters (`charId`) ON DELETE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;