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
package org.l2j.gameserver.mobius.gameserver.instancemanager;

import com.l2jmobius.Config;
import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.util.IGameXmlReader;
import org.l2j.gameserver.mobius.gameserver.model.CursedWeapon;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Attackable;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.*;
import org.l2j.gameserver.mobius.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.mobius.gameserver.util.Broadcast;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * UnAfraid: TODO: Rewrite with DocumentParser
 *
 * @author Micht
 */
public final class CursedWeaponsManager implements IGameXmlReader {
    private static final Logger LOGGER = Logger.getLogger(CursedWeaponsManager.class.getName());

    private final Map<Integer, CursedWeapon> _cursedWeapons = new HashMap<>();

    protected CursedWeaponsManager() {
        load();
    }

    public static void announce(SystemMessage sm) {
        Broadcast.toAllOnlinePlayers(sm);
    }

    public static void removeFromDb(int itemId) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM cursed_weapons WHERE itemId = ?")) {
            ps.setInt(1, itemId);
            ps.executeUpdate();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Failed to remove data: " + e.getMessage(), e);
        }
    }

    public static CursedWeaponsManager getInstance() {
        return SingletonHolder._instance;
    }

    @Override
    public void load() {
        if (!Config.ALLOW_CURSED_WEAPONS) {
            return;
        }

        parseDatapackFile("data/CursedWeapons.xml");
        restore();
        controlPlayers();
        LOGGER.info(getClass().getSimpleName() + ": Loaded: " + _cursedWeapons.size() + " cursed weapon(s).");
    }

    @Override
    public void parseDocument(Document doc, File f) {
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("item".equalsIgnoreCase(d.getNodeName())) {
                        NamedNodeMap attrs = d.getAttributes();
                        final int id = Integer.parseInt(attrs.getNamedItem("id").getNodeValue());
                        final int skillId = Integer.parseInt(attrs.getNamedItem("skillId").getNodeValue());
                        final String name = attrs.getNamedItem("name").getNodeValue();

                        final CursedWeapon cw = new CursedWeapon(id, skillId, name);

                        int val;
                        for (Node cd = d.getFirstChild(); cd != null; cd = cd.getNextSibling()) {
                            if ("dropRate".equalsIgnoreCase(cd.getNodeName())) {
                                attrs = cd.getAttributes();
                                val = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                                cw.setDropRate(val);
                            } else if ("duration".equalsIgnoreCase(cd.getNodeName())) {
                                attrs = cd.getAttributes();
                                val = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                                cw.setDuration(val);
                            } else if ("durationLost".equalsIgnoreCase(cd.getNodeName())) {
                                attrs = cd.getAttributes();
                                val = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                                cw.setDurationLost(val);
                            } else if ("disapearChance".equalsIgnoreCase(cd.getNodeName())) {
                                attrs = cd.getAttributes();
                                val = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                                cw.setDisapearChance(val);
                            } else if ("stageKills".equalsIgnoreCase(cd.getNodeName())) {
                                attrs = cd.getAttributes();
                                val = Integer.parseInt(attrs.getNamedItem("val").getNodeValue());
                                cw.setStageKills(val);
                            }
                        }

                        // Store cursed weapon
                        _cursedWeapons.put(id, cw);
                    }
                }
            }
        }
    }

    private void restore() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement s = con.createStatement();
             ResultSet rs = s.executeQuery("SELECT itemId, charId, playerReputation, playerPkKills, nbKills, endTime FROM cursed_weapons")) {
            // Retrieve the L2PcInstance from the characters table of the database
            CursedWeapon cw;
            while (rs.next()) {
                cw = _cursedWeapons.get(rs.getInt("itemId"));
                cw.setPlayerId(rs.getInt("charId"));
                cw.setPlayerReputation(rs.getInt("playerReputation"));
                cw.setPlayerPkKills(rs.getInt("playerPkKills"));
                cw.setNbKills(rs.getInt("nbKills"));
                cw.setEndTime(rs.getLong("endTime"));
                cw.reActivate();
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, getClass().getSimpleName() + ": Could not restore CursedWeapons data: ", e);
        }
    }

    private void controlPlayers() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT owner_id FROM items WHERE item_id=?")) {
            // TODO: See comments below...
            // This entire for loop should NOT be necessary, since it is already handled by
            // CursedWeapon.endOfLife(). However, if we indeed *need* to duplicate it for safety,
            // then we'd better make sure that it FULLY cleans up inactive cursed weapons!
            // Undesired effects result otherwise, such as player with no zariche but with karma
            // or a lost-child entry in the cursed weapons table, without a corresponding one in items...
            for (CursedWeapon cw : _cursedWeapons.values()) {
                if (cw.isActivated()) {
                    continue;
                }

                // Do an item check to be sure that the cursed weapon isn't hold by someone
                final int itemId = cw.getItemId();
                ps.setInt(1, itemId);
                try (ResultSet rset = ps.executeQuery()) {
                    if (rset.next()) {
                        // A player has the cursed weapon in his inventory ...
                        final int playerId = rset.getInt("owner_id");
                        LOGGER.info("PROBLEM : Player " + playerId + " owns the cursed weapon " + itemId + " but he shouldn't.");

                        // Delete the item
                        try (PreparedStatement delete = con.prepareStatement("DELETE FROM items WHERE owner_id=? AND item_id=?")) {
                            delete.setInt(1, playerId);
                            delete.setInt(2, itemId);
                            if (delete.executeUpdate() != 1) {
                                LOGGER.warning("Error while deleting cursed weapon " + itemId + " from userId " + playerId);
                            }
                        }

                        // Restore the player's old karma and pk count
                        try (PreparedStatement update = con.prepareStatement("UPDATE characters SET reputation=?, pkkills=? WHERE charId=?")) {
                            update.setInt(1, cw.getPlayerReputation());
                            update.setInt(2, cw.getPlayerPkKills());
                            update.setInt(3, playerId);
                            if (update.executeUpdate() != 1) {
                                LOGGER.warning("Error while updating karma & pkkills for userId " + cw.getPlayerId());
                            }
                        }
                        // clean up the cursed weapons table.
                        removeFromDb(itemId);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not check CursedWeapons data: ", e);
        }
    }

    public synchronized void checkDrop(L2Attackable attackable, L2PcInstance player) {
        if ((attackable instanceof L2DefenderInstance) || (attackable instanceof L2GuardInstance) || (attackable instanceof L2GrandBossInstance) || (attackable instanceof L2FeedableBeastInstance) || (attackable instanceof L2FortCommanderInstance)) {
            return;
        }

        for (CursedWeapon cw : _cursedWeapons.values()) {
            if (cw.isActive()) {
                continue;
            }

            if (cw.checkDrop(attackable, player)) {
                break;
            }
        }
    }

    public void activate(L2PcInstance player, L2ItemInstance item) {
        final CursedWeapon cw = _cursedWeapons.get(item.getId());
        if (player.isCursedWeaponEquipped()) // cannot own 2 cursed swords
        {
            final CursedWeapon cw2 = _cursedWeapons.get(player.getCursedWeaponEquippedId());
            // TODO: give the bonus level in a more appropriate manner.
            // The following code adds "_stageKills" levels. This will also show in the char status.
            // I do not have enough info to know if the bonus should be shown in the pk count, or if it
            // should be a full "_stageKills" bonus or just the remaining from the current count till the of the current stage...
            // This code is a TEMP fix, so that the cursed weapon's bonus level can be observed with as little change in the code as possible, until proper info arises.
            cw2.setNbKills(cw2.getStageKills() - 1);
            cw2.increaseKills();

            // erase the newly obtained cursed weapon
            cw.setPlayer(player); // NECESSARY in order to find which inventory the weapon is in!
            cw.endOfLife(); // expire the weapon and clean up.
        } else {
            cw.activate(player, item);
        }
    }

    public void drop(int itemId, L2Character killer) {
        final CursedWeapon cw = _cursedWeapons.get(itemId);

        cw.dropIt(killer);
    }

    public void increaseKills(int itemId) {
        final CursedWeapon cw = _cursedWeapons.get(itemId);

        cw.increaseKills();
    }

    public int getLevel(int itemId) {
        final CursedWeapon cw = _cursedWeapons.get(itemId);

        return cw.getLevel();
    }

    public void checkPlayer(L2PcInstance player) {
        if (player == null) {
            return;
        }

        for (CursedWeapon cw : _cursedWeapons.values()) {
            if (cw.isActivated() && (player.getObjectId() == cw.getPlayerId())) {
                cw.setPlayer(player);
                cw.setItem(player.getInventory().getItemByItemId(cw.getItemId()));
                cw.giveSkill();
                player.setCursedWeaponEquippedId(cw.getItemId());

                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_S2_MINUTE_S_OF_USAGE_TIME_REMAINING);
                sm.addString(cw.getName());
                // sm.addItemName(cw.getItemId());
                sm.addInt((int) ((cw.getEndTime() - System.currentTimeMillis()) / 60000));
                player.sendPacket(sm);
            }
        }
    }

    public int checkOwnsWeaponId(int ownerId) {
        for (CursedWeapon cw : _cursedWeapons.values()) {
            if (cw.isActivated() && (ownerId == cw.getPlayerId())) {
                return cw.getItemId();
            }
        }
        return -1;
    }

    public void saveData() {
        for (CursedWeapon cw : _cursedWeapons.values()) {
            cw.saveData();
        }
    }

    public boolean isCursed(int itemId) {
        return _cursedWeapons.containsKey(itemId);
    }

    public Collection<CursedWeapon> getCursedWeapons() {
        return _cursedWeapons.values();
    }

    public Set<Integer> getCursedWeaponsIds() {
        return _cursedWeapons.keySet();
    }

    public CursedWeapon getCursedWeapon(int itemId) {
        return _cursedWeapons.get(itemId);
    }

    public void givePassive(int itemId) {
        try {
            _cursedWeapons.get(itemId).giveSkill();
        } catch (Exception e) {
            /***/
        }
    }

    private static class SingletonHolder {
        protected static final CursedWeaponsManager _instance = new CursedWeaponsManager();
    }
}
