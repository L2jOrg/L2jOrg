package org.l2j.gameserver.data.dao;

import org.l2j.commons.database.DAO;
import org.l2j.commons.database.annotation.Query;

public interface IMailDAO extends DAO {

    @Query("DELETE FROM mail WHERE mail.message_id NOT IN (SELECT message_id FROM character_mail)")
    int deleteWithoutPlayer();
}
