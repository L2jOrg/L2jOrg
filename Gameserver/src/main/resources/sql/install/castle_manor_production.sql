DROP TABLE IF EXISTS `castle_manor_production`;
CREATE TABLE IF NOT EXISTS `castle_manor_production` (
 `castle_id` TINYINT(3) UNSIGNED NOT NULL DEFAULT '0',
 `seed_id` INT(11) UNSIGNED NOT NULL DEFAULT '0',
 `amount` INT(11) UNSIGNED NOT NULL DEFAULT '0',
 `start_amount` INT(11) UNSIGNED NOT NULL DEFAULT '0',
 `price` INT(11) UNSIGNED NOT NULL DEFAULT '0',
 `next_period` TINYINT(1) UNSIGNED NOT NULL DEFAULT '1',
 PRIMARY KEY (`castle_id`, `seed_id`, `next_period`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;