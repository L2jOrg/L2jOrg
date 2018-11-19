UPDATE `character_variables` SET `value` = '1' WHERE `name` = 'pa_items_recieved' AND `value` = 'true';
DELETE FROM `character_variables` WHERE `name` = 'pa_items_recieved';