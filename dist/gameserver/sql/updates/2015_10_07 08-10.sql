UPDATE characters SET rec_have = 0, rec_left = 0;
ALTER TABLE characters CHANGE `rec_left` `rec_left` TINYINT UNSIGNED NOT NULL DEFAULT '0';