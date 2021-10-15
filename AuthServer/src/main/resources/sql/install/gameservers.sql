CREATE TABLE IF NOT EXISTS `gameservers` (
  `id` INT NOT NULL,
  `key` VARCHAR(64) NOT NULL,
  `type` INT,
  PRIMARY KEY (`id`)
);