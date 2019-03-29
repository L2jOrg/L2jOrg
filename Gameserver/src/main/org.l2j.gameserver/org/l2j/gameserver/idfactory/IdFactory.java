package org.l2j.gameserver.idfactory;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.data.database.dao.AccountVariableDAO;
import org.l2j.gameserver.data.database.dao.CharacterDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

public abstract class IdFactory {

    protected final Logger LOGGER = LoggerFactory.getLogger(getClass().getName());
    
    static final int FIRST_OID = 0x0000001;
    private static final int LAST_OID = 0x7FFFFFFF;
    static final int FREE_OBJECT_ID_SIZE = LAST_OID - FIRST_OID;

    private static final String[][] ID_EXTRACTS = {
        {"characters", "charId"},
        {"items", "object_id"},
        {"clan_data", "clan_id"},
        {"itemsonground", "object_id"},
        {"messages", "messageId"}
    };

    private static final String[] TIMESTAMPS_CLEAN = {
        "DELETE FROM character_instance_time WHERE time <= ?",
        "DELETE FROM character_skills_save WHERE restore_type = 1 AND systime <= ?"
    };
    
    boolean initialized;

    protected IdFactory() {
        getDAO(CharacterDAO.class).setAllCharactersOffline();
        cleanUpDB();
        cleanUpTimeStamps();
    }

    /**
     * Cleans up Database
     */
    private void cleanUpDB() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement stmt = con.createStatement()) {
            final long cleanupStart = System.currentTimeMillis();
            int cleanCount = 0;

            getDAO(AccountVariableDAO.class).deleteWithoutAccount();
            // Items
            cleanCount += stmt.executeUpdate("DELETE FROM items WHERE items.owner_id NOT IN (SELECT charId FROM characters) AND items.owner_id NOT IN (SELECT clan_id FROM clan_data) AND items.owner_id != -1;");
            cleanCount += stmt.executeUpdate("DELETE FROM items WHERE items.owner_id = -1 AND loc LIKE 'MAIL' AND loc_data NOT IN (SELECT messageId FROM messages WHERE senderId = -1);");

            // Misc
            cleanCount += stmt.executeUpdate("DELETE FROM clan_data WHERE clan_data.clan_id NOT IN (SELECT clanid FROM characters);");

            // Forum Related
            cleanCount += stmt.executeUpdate("DELETE FROM forums WHERE forums.forum_owner_id NOT IN (SELECT clan_id FROM clan_data) AND forums.forum_parent=2;");
            cleanCount += stmt.executeUpdate("DELETE FROM forums WHERE forums.forum_owner_id NOT IN (SELECT charId FROM characters) AND forums.forum_parent=3;");

            // Update needed items after cleaning has taken place.
            stmt.executeUpdate("UPDATE clan_data SET auction_bid_at = 0 WHERE auction_bid_at NOT IN (SELECT auctionId FROM auction_bid);");
            stmt.executeUpdate("UPDATE clan_data SET new_leader_id = 0 WHERE new_leader_id <> 0 AND new_leader_id NOT IN (SELECT charId FROM characters);");
            stmt.executeUpdate("UPDATE clan_subpledges SET leader_id=0 WHERE clan_subpledges.leader_id NOT IN (SELECT charId FROM characters) AND leader_id > 0;");
            stmt.executeUpdate("UPDATE castle SET side='NEUTRAL' WHERE castle.id NOT IN (SELECT hasCastle FROM clan_data);");
            stmt.executeUpdate("UPDATE characters SET clanid=0, clan_privs=0, wantspeace=0, subpledge=0, lvl_joined_academy=0, apprentice=0, sponsor=0, clan_join_expiry_time=0, clan_create_expiry_time=0 WHERE characters.clanid > 0 AND characters.clanid NOT IN (SELECT clan_id FROM clan_data);");
            stmt.executeUpdate("UPDATE fort SET owner=0 WHERE owner NOT IN (SELECT clan_id FROM clan_data);");

            LOGGER.info("Cleaned {} elements from database in {} s", cleanCount, (System.currentTimeMillis() - cleanupStart) / 1000);
        } catch (SQLException e) {
            LOGGER.warn("Could not clean up database", e);
        }
    }

    private void cleanUpTimeStamps() {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            int cleanCount = 0;
            for (String line : TIMESTAMPS_CLEAN) {
                try (PreparedStatement stmt = con.prepareStatement(line)) {
                    stmt.setLong(1, System.currentTimeMillis());
                    cleanCount += stmt.executeUpdate();
                }
            }
            LOGGER.info("Cleaned " + cleanCount + " expired timestamps from database.");
        } catch (SQLException e) {
        }
    }

    /**
     * @return
     * @throws Exception
     * @throws SQLException
     */
    protected final Integer[] extractUsedObjectIDTable() throws Exception {
        final List<Integer> temp = new ArrayList<>();
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement s = con.createStatement()) {
            String extractUsedObjectIdsQuery = "";

            for (String[] tblClmn : ID_EXTRACTS) {
                extractUsedObjectIdsQuery += "SELECT " + tblClmn[1] + " FROM " + tblClmn[0] + " UNION ";
            }

            extractUsedObjectIdsQuery = extractUsedObjectIdsQuery.substring(0, extractUsedObjectIdsQuery.length() - 7); // Remove the last " UNION "
            try (ResultSet rs = s.executeQuery(extractUsedObjectIdsQuery)) {
                while (rs.next()) {
                    temp.add(rs.getInt(1));
                }
            }
        }
        Collections.sort(temp);
        return temp.toArray(Integer[]::new);
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
