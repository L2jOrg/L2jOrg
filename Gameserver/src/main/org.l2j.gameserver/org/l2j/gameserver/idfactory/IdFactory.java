package org.l2j.gameserver.idfactory;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.Config;
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
        if (Config.DATABASE_CLEAN_UP) {
            cleanUpDB();
        }
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

            // If the character does not exist...
            // Characters
            cleanCount += stmt.executeUpdate("DELETE FROM account_gsdata WHERE account_gsdata.account_name NOT IN (SELECT account_name FROM characters);");

            // Items
            cleanCount += stmt.executeUpdate("DELETE FROM items WHERE items.owner_id NOT IN (SELECT charId FROM characters) AND items.owner_id NOT IN (SELECT clan_id FROM clan_data) AND items.owner_id != -1;");
            cleanCount += stmt.executeUpdate("DELETE FROM items WHERE items.owner_id = -1 AND loc LIKE 'MAIL' AND loc_data NOT IN (SELECT messageId FROM messages WHERE senderId = -1);");

            cleanCount += stmt.executeUpdate("DELETE FROM item_auction_bid WHERE item_auction_bid.playerObjId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM item_variations WHERE item_variations.itemId NOT IN (SELECT object_id FROM items);");
            cleanCount += stmt.executeUpdate("DELETE FROM item_elementals WHERE item_elementals.itemId NOT IN (SELECT object_id FROM items);");
            cleanCount += stmt.executeUpdate("DELETE FROM item_special_abilities WHERE item_special_abilities.objectId NOT IN (SELECT object_id FROM items);");
            cleanCount += stmt.executeUpdate("DELETE FROM item_variables WHERE item_variables.id NOT IN (SELECT object_id FROM items);");

            // Misc
            cleanCount += stmt.executeUpdate("DELETE FROM cursed_weapons WHERE cursed_weapons.charId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM heroes WHERE heroes.charId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM olympiad_nobles WHERE olympiad_nobles.charId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM olympiad_nobles_eom WHERE olympiad_nobles_eom.charId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM pets WHERE pets.item_obj_id NOT IN (SELECT object_id FROM items);");
            cleanCount += stmt.executeUpdate("DELETE FROM merchant_lease WHERE merchant_lease.player_id NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_reco_bonus WHERE character_reco_bonus.charId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM clan_data WHERE clan_data.leader_id NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM clan_data WHERE clan_data.clan_id NOT IN (SELECT clanid FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM olympiad_fights WHERE olympiad_fights.charOneId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM olympiad_fights WHERE olympiad_fights.charTwoId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM heroes_diary WHERE heroes_diary.charId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_offline_trade WHERE character_offline_trade.charId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_offline_trade_items WHERE character_offline_trade_items.charId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_tpbookmark WHERE character_tpbookmark.charId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_variables WHERE character_variables.charId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM bot_reported_char_data WHERE bot_reported_char_data.botId NOT IN (SELECT charId FROM characters);");

            // If the clan does not exist...
            cleanCount += stmt.executeUpdate("DELETE FROM clan_privs WHERE clan_privs.clan_id NOT IN (SELECT clan_id FROM clan_data);");
            cleanCount += stmt.executeUpdate("DELETE FROM clan_skills WHERE clan_skills.clan_id NOT IN (SELECT clan_id FROM clan_data);");
            cleanCount += stmt.executeUpdate("DELETE FROM clan_subpledges WHERE clan_subpledges.clan_id NOT IN (SELECT clan_id FROM clan_data);");
            cleanCount += stmt.executeUpdate("DELETE FROM clan_wars WHERE clan_wars.clan1 NOT IN (SELECT clan_id FROM clan_data);");
            cleanCount += stmt.executeUpdate("DELETE FROM clan_wars WHERE clan_wars.clan2 NOT IN (SELECT clan_id FROM clan_data);");
            cleanCount += stmt.executeUpdate("DELETE FROM siege_clans WHERE siege_clans.clan_id NOT IN (SELECT clan_id FROM clan_data);");
            cleanCount += stmt.executeUpdate("DELETE FROM clan_notices WHERE clan_notices.clan_id NOT IN (SELECT clan_id FROM clan_data);");
            cleanCount += stmt.executeUpdate("DELETE FROM auction_bid WHERE auction_bid.bidderId NOT IN (SELECT clan_id FROM clan_data);");

            // Forum Related
            cleanCount += stmt.executeUpdate("DELETE FROM forums WHERE forums.forum_owner_id NOT IN (SELECT clan_id FROM clan_data) AND forums.forum_parent=2;");
            cleanCount += stmt.executeUpdate("DELETE FROM forums WHERE forums.forum_owner_id NOT IN (SELECT charId FROM characters) AND forums.forum_parent=3;");
            cleanCount += stmt.executeUpdate("DELETE FROM posts WHERE posts.post_forum_id NOT IN (SELECT forum_id FROM forums);");
            cleanCount += stmt.executeUpdate("DELETE FROM topic WHERE topic.topic_forum_id NOT IN (SELECT forum_id FROM forums);");

            // Update needed items after cleaning has taken place.
            stmt.executeUpdate("UPDATE clan_data SET auction_bid_at = 0 WHERE auction_bid_at NOT IN (SELECT auctionId FROM auction_bid);");
            stmt.executeUpdate("UPDATE clan_data SET new_leader_id = 0 WHERE new_leader_id <> 0 AND new_leader_id NOT IN (SELECT charId FROM characters);");
            stmt.executeUpdate("UPDATE clan_subpledges SET leader_id=0 WHERE clan_subpledges.leader_id NOT IN (SELECT charId FROM characters) AND leader_id > 0;");
            stmt.executeUpdate("UPDATE castle SET side='NEUTRAL' WHERE castle.id NOT IN (SELECT hasCastle FROM clan_data);");
            stmt.executeUpdate("UPDATE characters SET clanid=0, clan_privs=0, wantspeace=0, subpledge=0, lvl_joined_academy=0, apprentice=0, sponsor=0, clan_join_expiry_time=0, clan_create_expiry_time=0 WHERE characters.clanid > 0 AND characters.clanid NOT IN (SELECT clan_id FROM clan_data);");
            stmt.executeUpdate("UPDATE fort SET owner=0 WHERE owner NOT IN (SELECT clan_id FROM clan_data);");

            LOGGER.info("Cleaned " + cleanCount + " elements from database in " + ((System.currentTimeMillis() - cleanupStart) / 1000) + " s");
        } catch (SQLException e) {
            LOGGER.warn("Could not clean up database: " + e.getMessage(), e);
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
