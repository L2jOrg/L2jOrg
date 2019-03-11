/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.idfactory;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.Config;

import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import static java.util.Objects.isNull;

/**
 * This class ...
 *
 * @version $Revision: 1.3.2.1.2.7 $ $Date: 2005/04/11 10:06:12 $
 */
public abstract class IdFactory {
    public static final int FIRST_OID = 0x10000000;
    public static final int LAST_OID = 0x7FFFFFFF;
    public static final int FREE_OBJECT_ID_SIZE = LAST_OID - FIRST_OID;
    //@formatter:on
    protected static final String[] ID_CHECKS =
            {
                    "SELECT owner_id    FROM items                 WHERE object_id >= ?   AND object_id < ?",
                    "SELECT object_id   FROM items                 WHERE object_id >= ?   AND object_id < ?",
                    "SELECT charId     FROM character_quests      WHERE charId >= ?     AND charId < ?",
                    "SELECT charId     FROM character_contacts    WHERE charId >= ?     AND charId < ?",
                    "SELECT contactId  FROM character_contacts    WHERE contactId >= ?  AND contactId < ?",
                    "SELECT charId     FROM character_friends     WHERE charId >= ?     AND charId < ?",
                    "SELECT charId     FROM character_friends     WHERE friendId >= ?   AND friendId < ?",
                    "SELECT charId     FROM character_hennas      WHERE charId >= ? AND charId < ?",
                    "SELECT charId     FROM character_recipebook  WHERE charId >= ?     AND charId < ?",
                    "SELECT charId     FROM character_recipeshoplist  WHERE charId >= ?     AND charId < ?",
                    "SELECT charId     FROM character_shortcuts   WHERE charId >= ? AND charId < ?",
                    "SELECT charId     FROM character_macroses    WHERE charId >= ? AND charId < ?",
                    "SELECT charId     FROM character_skills      WHERE charId >= ? AND charId < ?",
                    "SELECT charId     FROM character_skills_save WHERE charId >= ? AND charId < ?",
                    "SELECT charId     FROM character_subclasses  WHERE charId >= ? AND charId < ?",
                    "SELECT charId      FROM characters            WHERE charId >= ?      AND charId < ?",
                    "SELECT clanid      FROM characters            WHERE clanid >= ?      AND clanid < ?",
                    "SELECT clan_id     FROM clan_data             WHERE clan_id >= ?     AND clan_id < ?",
                    "SELECT clan_id     FROM siege_clans           WHERE clan_id >= ?     AND clan_id < ?",
                    "SELECT ally_id     FROM clan_data             WHERE ally_id >= ?     AND ally_id < ?",
                    "SELECT leader_id   FROM clan_data             WHERE leader_id >= ?   AND leader_id < ?",
                    "SELECT item_obj_id FROM pets                  WHERE item_obj_id >= ? AND item_obj_id < ?",
                    "SELECT object_id   FROM itemsonground        WHERE object_id >= ?   AND object_id < ?",
                    "SELECT summonId	FROM characters_summons	WHERE summonId >= ?	AND summonId < ?"
            };
    //@formatter:off
    private static final String[][] ID_EXTRACTS =
            {
                    {"characters", "charId"},
                    {"items", "object_id"},
                    {"clan_data", "clan_id"},
                    {"itemsonground", "object_id"},
                    {"messages", "messageId"}
            };
    private static final String[] TIMESTAMPS_CLEAN =
            {
                    "DELETE FROM character_instance_time WHERE time <= ?",
                    "DELETE FROM character_skills_save WHERE restore_type = 1 AND systime <= ?"
            };
    protected static IdFactory _instance;
    protected final Logger LOGGER = Logger.getLogger(getClass().getName());
    protected boolean _initialized;

    protected IdFactory() {
        setAllCharacterOffline();
        if (Config.DATABASE_CLEAN_UP) {
            cleanUpDB();
        }
        cleanUpTimeStamps();
    }

    public static IdFactory getInstance() {
        if (isNull(_instance)) {
            _instance = new BitSetIDFactory();
        }
        return _instance;
    }

    /**
     * Sets all character offline
     */
    private void setAllCharacterOffline() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement s = con.createStatement()) {
            s.executeUpdate("UPDATE characters SET online = 0");
            LOGGER.info("Updated characters online status.");
        } catch (SQLException e) {
            LOGGER.log(Level.WARNING, "Could not update characters online status: " + e.getMessage(), e);
        }
    }

    /**
     * Cleans up Database
     */
    private void cleanUpDB() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement stmt = con.createStatement()) {
            final long cleanupStart = System.currentTimeMillis();
            int cleanCount = 0;
            // Misc/Account Related
            // Please read the descriptions above each before uncommenting them. If you are still
            // unsure of what exactly it does, leave it commented out. This is for those who know
            // what they are doing. :)

            // Deletes only accounts that HAVE been logged into and have no characters associated
            // with the account.
            // cleanCount +=
            // stmt.executeUpdate("DELETE FROM accounts WHERE accounts.lastactive > 0 AND accounts.login NOT IN (SELECT account_name FROM characters);");

            // Deletes any accounts that don't have characters. Whether or not the player has ever
            // logged into the account.
            // cleanCount +=
            // stmt.executeUpdate("DELETE FROM accounts WHERE accounts.login NOT IN (SELECT account_name FROM characters);");

            // Deletes banned accounts that have not been logged into for xx amount of days
            // (specified at the end of the script, default is set to 90 days). This prevents
            // accounts from being deleted that were accidentally or temporarily banned.
            // cleanCount +=
            // stmt.executeUpdate("DELETE FROM accounts WHERE accounts.accessLevel < 0 AND DATEDIFF(CURRENT_DATE( ) , FROM_UNIXTIME(`lastactive`/1000)) > 90;");
            // cleanCount +=
            // stmt.executeUpdate("DELETE FROM characters WHERE characters.account_name NOT IN (SELECT login FROM accounts);");

            // If the character does not exist...
            // Characters
            cleanCount += stmt.executeUpdate("DELETE FROM account_gsdata WHERE account_gsdata.account_name NOT IN (SELECT account_name FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_contacts WHERE character_contacts.charId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_contacts WHERE character_contacts.contactId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_friends WHERE character_friends.charId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_friends WHERE character_friends.friendId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_hennas WHERE character_hennas.charId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_macroses WHERE character_macroses.charId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_quests WHERE character_quests.charId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_recipebook WHERE character_recipebook.charId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_recipeshoplist WHERE character_recipeshoplist.charId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_shortcuts WHERE character_shortcuts.charId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_skills WHERE character_skills.charId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_skills_save WHERE character_skills_save.charId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_subclasses WHERE character_subclasses.charId NOT IN (SELECT charId FROM characters);");
            cleanCount += stmt.executeUpdate("DELETE FROM character_instance_time WHERE character_instance_time.charId NOT IN (SELECT charId FROM characters);");

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
            LOGGER.log(Level.WARNING, "Could not clean up database: " + e.getMessage(), e);
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
        return temp.toArray(new Integer[temp.size()]);
    }

    public boolean isInitialized() {
        return _initialized;
    }

    public abstract int getNextId();

    /**
     * return a used Object ID back to the pool
     *
     * @param id
     */
    public abstract void releaseId(int id);

    public abstract int size();
}
