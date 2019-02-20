package org.l2j.gameserver.idfactory;

import org.l2j.commons.database.L2DatabaseFactory;
import org.l2j.gameserver.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import static java.util.Objects.isNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

public abstract class IdFactory
{
    private static final Logger logger = LoggerFactory.getLogger(IdFactory.class);
    private static IdFactory instance;

    protected static final int FIRST_OID = 0x10000000;
    protected static final int LAST_OID = 0x7FFFFFFF;
    protected static final int FREE_OBJECT_ID_SIZE = LAST_OID - FIRST_OID;

    protected boolean initialized;
    protected long releasedCount = 0;

    protected IdFactory() {
        resetOnlineStatus();
        globalRemoveItems();
        cleanUpDB();
    }

    private void resetOnlineStatus() {
        logger.info("Clearing characters online status.");
        getDAO(ICharacterDAO.class).updateCharactersOfflineStatus();
    }

    private void globalRemoveItems() {
        logger.info("Global removed {} items.", getDAO(IItemsDAO.class).deleteGlobalItemsToRemove());
    }

    private void cleanUpDB() {
        try(var con = L2DatabaseFactory.getInstance().getConnection();
            var st = con.createStatement()) {

            long cleanupStart = System.currentTimeMillis();
            int cleanCount = 0;

            //
            //Начинаем чистить таблицы последствия после удаления персонажа.
            //

            //Чистим по аккаунту.
            cleanCount += st.executeUpdate("DELETE FROM premium_accounts WHERE premium_accounts.account NOT IN (SELECT account_name FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM account_variables WHERE account_variables.account_name NOT IN (SELECT account_name FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM bbs_memo WHERE bbs_memo.account_name NOT IN (SELECT account_name FROM characters);");

            //Чистим по Object ID персонажа.

            cleanCount += getDAO(IItemsDAO.class).deleteItemsWithoutOwner();

            //Clean clans and alliances.
            cleanCount += st.executeUpdate("DELETE FROM clan_data WHERE clan_data.clan_id NOT IN (SELECT clanid FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM clan_subpledges WHERE clan_subpledges.type = 0 AND clan_subpledges.leader_id NOT IN (SELECT obj_Id FROM characters);");
            cleanCount += st.executeUpdate("DELETE FROM clan_data WHERE clan_data.clan_id NOT IN (SELECT clan_id FROM clan_subpledges WHERE clan_subpledges.type = 0);");
            cleanCount += st.executeUpdate("DELETE FROM ally_data WHERE ally_data.ally_id NOT IN (SELECT ally_id FROM clan_data);");
            cleanCount += st.executeUpdate("DELETE FROM clan_subpledges WHERE clan_subpledges.clan_id NOT IN (SELECT clan_id FROM clan_data);");

            //Чистим почту.

            cleanCount += st.executeUpdate("DELETE FROM mail WHERE mail.message_id NOT IN (SELECT message_id FROM character_mail);");

            st.executeUpdate("UPDATE clan_subpledges SET leader_id=0 WHERE leader_id > 0 AND clan_subpledges.leader_id NOT IN (SELECT obj_Id FROM characters);");
            st.executeUpdate("UPDATE characters SET clanid = '0', title = '', pledge_type = '0', pledge_rank = '0', lvl_joined_academy = '0', apprentice = '0' WHERE characters.clanid > 0 AND characters.clanid NOT IN (SELECT clan_id FROM clan_data);");
            st.executeUpdate("UPDATE items SET loc = 'WAREHOUSE' WHERE loc = 'MAIL' AND items.object_id NOT IN (SELECT item_id FROM mail_attachments);");

            logger.info("IdFactory: Cleaned {}  elements from database in {} sec.", cleanCount, (System.currentTimeMillis() - cleanupStart) / 1000);
        } catch(SQLException e) {
            logger.error(e.getLocalizedMessage(), e);
        }
    }

    protected int[] extractUsedObjectIDTable() {
        return getDAO(IdFactoryDAO.class).findUsedObjectIds().toArray();
    }

    public boolean isInitialized() {
        return initialized;
    }

    public abstract int getNextId();

    /**
     * return a used Object ID back to the pool
     * @param id ID
     */
    public void releaseId(int id)
    {
        releasedCount++;
    }

    public abstract int size();

    public static IdFactory getInstance() {
        if(isNull(instance)){
            instance = new BitSetIDFactory();
        }
        return instance;
    }
}