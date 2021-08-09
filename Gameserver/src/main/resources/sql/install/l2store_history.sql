DROP TABLE IF EXISTS l2store_history;
CREATE TABLE l2store_history (
    `product_id` INT  NOT NULL,
    `count`      INT  NOT NULL DEFAULT '1',
    `account`    VARCHAR(45) NOT NULL,
    `sell_date`  DATE NOT NULL DEFAULT (CURRENT_DATE),
    PRIMARY KEY productId (`product_id`, `account`, `sell_date`),
    FOREIGN KEY (`account`) REFERENCES account_data (`account`) ON DELETE CASCADE
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;