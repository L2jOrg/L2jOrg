package org.l2j.gameserver.idfactory;

import org.l2j.commons.database.L2DatabaseFactory;
import org.l2j.gameserver.data.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

import static java.util.Objects.isNull;
import static org.l2j.commons.database.DatabaseAccess.getDAO;

public abstract class IdFactory {
    private static final Logger logger = LoggerFactory.getLogger(IdFactory.class);
    private static IdFactory instance;

    protected static final int FIRST_OID = 0x10000000;
    protected static final int LAST_OID = 0x7FFFFFFF;
    protected static final int FREE_OBJECT_ID_SIZE = LAST_OID - FIRST_OID;

    protected boolean initialized;
    private long releasedCount = 0;

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

            cleanCount += getDAO(IItemsDAO.class).deleteItemsWithoutOwner();

            //Clean clans and alliances.
            cleanCount += getDAO(ClanDAO.class).deleteWithoutPlayers();
            cleanCount += getDAO(ClanDAO.class).deleteMainSubpledgeWithoutLeader();
            cleanCount += getDAO(ClanDAO.class).deleteClanWithoutMainSubpledge();
            cleanCount += getDAO(ClanDAO.class).deleteAllyWithoutClan();
            cleanCount += getDAO(ClanDAO.class).deleteSubpledgeWithoutClan();

            //Чистим почту.

            cleanCount += getDAO(IMailDAO.class).deleteWithoutPlayer();

            getDAO(ClanDAO.class).updateSubpledgeWithoutLeader();
            getDAO(ClanDAO.class).updateMemberInfoOfMissingClan();
            getDAO(IItemsDAO.class).updateItemMailWithoutAttachment();


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