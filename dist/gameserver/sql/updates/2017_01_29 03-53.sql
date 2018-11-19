CREATE TABLE IF NOT EXISTS `items_ensoul` (
  `object_id` int(11) NOT NULL,
  `type` tinyint(1) NOT NULL,
  `id` tinyint(3) NOT NULL,
  `ensoul_id` int(3) NOT NULL,
  PRIMARY KEY  (`object_id`, `type`, `id`),
  FOREIGN KEY (`object_id`) REFERENCES `items` (`object_id`) ON DELETE CASCADE
) ENGINE=InnoDB;