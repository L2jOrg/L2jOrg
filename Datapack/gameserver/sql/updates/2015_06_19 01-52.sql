UPDATE character_hennas SET character_hennas.draw_time = 1434669330 + character_hennas.slot WHERE character_hennas.draw_time = 0;
ALTER TABLE character_hennas DROP COLUMN slot;
ALTER TABLE character_hennas DROP INDEX char_obj_id;
ALTER TABLE character_hennas ADD PRIMARY KEY (`char_obj_id`,`class_index`,`draw_time`);