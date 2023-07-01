ALTER TABLE `player_variables`
ADD `ap_range_close` INT DEFAULT 600,
ADD `ap_range_long` INT DEFAULT 1400,
ADD `ap_show_range` BOOLEAN DEFAULT FALSE,
ADD `ap_anchored` BOOLEAN DEFAULT FALSE,
ADD `ap_anchor_x` INT DEFAULT 0,
ADD `ap_anchor_y` INT DEFAULT 0,
ADD `ap_anchor_z` INT DEFAULT 0;
