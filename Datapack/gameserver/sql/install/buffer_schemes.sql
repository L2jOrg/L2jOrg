CREATE TABLE IF NOT EXISTS `buffer_schemes` (
  `object_id` INT UNSIGNED NOT NULL DEFAULT '0',
  `scheme_name` VARCHAR(16) NOT NULL DEFAULT 'default',
  `skills` VARCHAR(200) NOT NULL,
    PRIMARY KEY (`object_id`,`scheme_name`)
) ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;;