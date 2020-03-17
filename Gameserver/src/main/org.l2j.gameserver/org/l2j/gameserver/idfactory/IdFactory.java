package org.l2j.gameserver.idfactory;

import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.data.database.dao.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

public abstract class IdFactory {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());
    
    static final int FIRST_OID = 0x0000001;
    private static final int LAST_OID = 0x7FFFFFFF;
    static final int FREE_OBJECT_ID_SIZE = LAST_OID - FIRST_OID;

    boolean initialized;

    protected IdFactory() {
        cleanUpDatabase();
        cleanUpTimeStamps();
    }

    private void cleanUpDatabase() {
        final long cleanupStart = System.currentTimeMillis();
        int cleanCount = 0;

        cleanCount += getDAO(AccountDAO.class).deleteWithoutAccount();

        var itemDAO = getDAO(ItemDAO.class);
        cleanCount += itemDAO.deleteWithoutOwner();
        cleanCount += itemDAO.deleteFromEmailWithoutMessage();


        var clanDao = getDAO(ClanDAO.class);
        cleanCount += clanDao.deleteWithoutMembers();

        cleanCount += getDAO(ForumDAO.class).deleteWithoutOwner();

        // Update needed items after cleaning has taken place.
        clanDao.resetAuctionBidWithoutAction();
        clanDao.resetNewLeaderWithoutCharacter();
        clanDao.resetSubpledgeLeaderWithoutCharacter();

        getDAO(CastleDAO.class).updateToNeutralWithoutOwner();
        getDAO(PlayerDAO.class).resetClanInfoOfNonexistentClan();
        getDAO(FortDAO.class).resetWithoutOwner();

        LOGGER.info("Cleaned {} elements from database in {} s", cleanCount, (System.currentTimeMillis() - cleanupStart) / 1000);
    }

    private void cleanUpTimeStamps() {
        var timestamp = System.currentTimeMillis();
        var characterDAO = getDAO(PlayerDAO.class);
        characterDAO.deleteExpiredInstances(timestamp);
        characterDAO.deleteExpiredSavedSkills(timestamp);
    }

    protected final IntSet extractUsedObjectIDTable()  {
        return getDAO(IdFactoryDAO.class).findUsedObjectIds();
    }

    public boolean isInitialized() {
        return initialized;
    }

    public abstract int getNextId();

    public abstract void releaseId(int id);

    public abstract int size();


    public static IdFactory getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final IdFactory INSTANCE = new BitSetIDFactory();
    }
}
