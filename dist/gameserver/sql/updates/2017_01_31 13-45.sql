ALTER TABLE bbs_buffs DROP PRIMARY KEY;
ALTER TABLE bbs_buffs ADD COLUMN `id` int(11) NOT NULL DEFAULT '0';
ALTER TABLE bbs_buffs ADD PRIMARY KEY (`id`, `char_id`,`name`);
ALTER TABLE bbs_buffs CHANGE `id` `id` int(11) NOT NULL auto_increment;