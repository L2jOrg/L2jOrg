DROP TABLE IF EXISTS `pet_skills_save`;
CREATE TABLE IF NOT EXISTS `pet_skills_save` (
  `item_id` INT NOT NULL,
  `skill_id` INT NOT NULL,
  `skill_level` INT NOT NULL DEFAULT 1,
  `remaining_time` INT NOT NULL,
  `buff_index` INT NOT NULL,
  PRIMARY KEY (`item_id`,`skill_id`,`skill_level`)
)  ENGINE=InnoDB DEFAULT CHARSET=UTF8MB4;