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
package org.l2j.gameserver.mobius.gameserver.data.xml.impl;

import org.l2j.commons.util.IGameXmlReader;
import org.l2j.gameserver.mobius.gameserver.model.L2AccessLevel;
import org.l2j.gameserver.mobius.gameserver.model.L2AdminCommandAccessRight;
import org.l2j.gameserver.mobius.gameserver.model.StatsSet;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.network.SystemMessageId;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.IClientOutgoingPacket;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.SystemMessage;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * Loads administrator access levels and commands.
 *
 * @author UnAfraid
 */
public final class AdminData implements IGameXmlReader {
    private static final Logger LOGGER = Logger.getLogger(AdminData.class.getName());

    private final Map<Integer, L2AccessLevel> _accessLevels = new HashMap<>();
    private final Map<String, L2AdminCommandAccessRight> _adminCommandAccessRights = new HashMap<>();
    private final Map<L2PcInstance, Boolean> _gmList = new ConcurrentHashMap<>();
    private int _highestLevel = 0;

    protected AdminData() {
        load();
    }

    /**
     * Gets the single instance of AdminTable.
     *
     * @return AccessLevels: the one and only instance of this class<br>
     */
    public static AdminData getInstance() {
        return SingletonHolder._instance;
    }

    @Override
    public synchronized void load() {
        _accessLevels.clear();
        _adminCommandAccessRights.clear();
        parseDatapackFile("config/AccessLevels.xml");
        LOGGER.info(getClass().getSimpleName() + ": Loaded: " + _accessLevels.size() + " Access Levels.");
        parseDatapackFile("config/AdminCommands.xml");
        LOGGER.info(getClass().getSimpleName() + ": Loaded: " + _adminCommandAccessRights.size() + " Access Commands.");
    }

    @Override
    public void parseDocument(Document doc, File f) {
        NamedNodeMap attrs;
        Node attr;
        StatsSet set;
        L2AccessLevel level;
        L2AdminCommandAccessRight command;
        for (Node n = doc.getFirstChild(); n != null; n = n.getNextSibling()) {
            if ("list".equalsIgnoreCase(n.getNodeName())) {
                for (Node d = n.getFirstChild(); d != null; d = d.getNextSibling()) {
                    if ("access".equalsIgnoreCase(d.getNodeName())) {
                        set = new StatsSet();
                        attrs = d.getAttributes();
                        for (int i = 0; i < attrs.getLength(); i++) {
                            attr = attrs.item(i);
                            set.set(attr.getNodeName(), attr.getNodeValue());
                        }
                        level = new L2AccessLevel(set);
                        if (level.getLevel() > _highestLevel) {
                            _highestLevel = level.getLevel();
                        }
                        _accessLevels.put(level.getLevel(), level);
                    } else if ("admin".equalsIgnoreCase(d.getNodeName())) {
                        set = new StatsSet();
                        attrs = d.getAttributes();
                        for (int i = 0; i < attrs.getLength(); i++) {
                            attr = attrs.item(i);
                            set.set(attr.getNodeName(), attr.getNodeValue());
                        }
                        command = new L2AdminCommandAccessRight(set);
                        _adminCommandAccessRights.put(command.getAdminCommand(), command);
                    }
                }
            }
        }
    }

    /**
     * Returns the access level by characterAccessLevel.
     *
     * @param accessLevelNum as int
     * @return the access level instance by char access level
     */
    public L2AccessLevel getAccessLevel(int accessLevelNum) {
        if (accessLevelNum < 0) {
            return _accessLevels.get(-1);
        }
        return _accessLevels.get(accessLevelNum);
    }

    /**
     * Gets the master access level.
     *
     * @return the master access level
     */
    public L2AccessLevel getMasterAccessLevel() {
        return _accessLevels.get(_highestLevel);
    }

    /**
     * Checks for access level.
     *
     * @param id the id
     * @return {@code true}, if successful, {@code false} otherwise
     */
    public boolean hasAccessLevel(int id) {
        return _accessLevels.containsKey(id);
    }

    /**
     * Checks for access.
     *
     * @param adminCommand the admin command
     * @param accessLevel  the access level
     * @return {@code true}, if successful, {@code false} otherwise
     */
    public boolean hasAccess(String adminCommand, L2AccessLevel accessLevel) {
        L2AdminCommandAccessRight acar = _adminCommandAccessRights.get(adminCommand);
        if (acar == null) {
            // Trying to avoid the spam for next time when the gm would try to use the same command
            if ((accessLevel.getLevel() > 0) && (accessLevel.getLevel() == _highestLevel)) {
                acar = new L2AdminCommandAccessRight(adminCommand, true, accessLevel.getLevel());
                _adminCommandAccessRights.put(adminCommand, acar);
                LOGGER.info(getClass().getSimpleName() + ": No rights defined for admin command " + adminCommand + " auto setting accesslevel: " + accessLevel.getLevel() + " !");
            } else {
                LOGGER.info(getClass().getSimpleName() + ": No rights defined for admin command " + adminCommand + " !");
                return false;
            }
        }
        return acar.hasAccess(accessLevel);
    }

    /**
     * Require confirm.
     *
     * @param command the command
     * @return {@code true}, if the command require confirmation, {@code false} otherwise
     */
    public boolean requireConfirm(String command) {
        final L2AdminCommandAccessRight acar = _adminCommandAccessRights.get(command);
        if (acar == null) {
            LOGGER.info(getClass().getSimpleName() + ": No rights defined for admin command " + command + ".");
            return false;
        }
        return acar.getRequireConfirm();
    }

    /**
     * Gets the all GMs.
     *
     * @param includeHidden the include hidden
     * @return the all GMs
     */
    public List<L2PcInstance> getAllGms(boolean includeHidden) {
        final List<L2PcInstance> tmpGmList = new ArrayList<>();
        for (Entry<L2PcInstance, Boolean> entry : _gmList.entrySet()) {
            if (includeHidden || !entry.getValue()) {
                tmpGmList.add(entry.getKey());
            }
        }
        return tmpGmList;
    }

    /**
     * Gets the all GM names.
     *
     * @param includeHidden the include hidden
     * @return the all GM names
     */
    public List<String> getAllGmNames(boolean includeHidden) {
        final List<String> tmpGmList = new ArrayList<>();
        for (Entry<L2PcInstance, Boolean> entry : _gmList.entrySet()) {
            if (!entry.getValue()) {
                tmpGmList.add(entry.getKey().getName());
            } else if (includeHidden) {
                tmpGmList.add(entry.getKey().getName() + " (invis)");
            }
        }
        return tmpGmList;
    }

    /**
     * Add a L2PcInstance player to the Set _gmList.
     *
     * @param player the player
     * @param hidden the hidden
     */
    public void addGm(L2PcInstance player, boolean hidden) {
        _gmList.put(player, hidden);
    }

    /**
     * Delete a GM.
     *
     * @param player the player
     */
    public void deleteGm(L2PcInstance player) {
        _gmList.remove(player);
    }

    /**
     * GM will be displayed on clients GM list.
     *
     * @param player the player
     */
    public void showGm(L2PcInstance player) {
        if (_gmList.containsKey(player)) {
            _gmList.put(player, false);
        }
    }

    /**
     * GM will no longer be displayed on clients GM list.
     *
     * @param player the player
     */
    public void hideGm(L2PcInstance player) {
        if (_gmList.containsKey(player)) {
            _gmList.put(player, true);
        }
    }

    /**
     * Checks if is GM online.
     *
     * @param includeHidden the include hidden
     * @return true, if is GM online
     */
    public boolean isGmOnline(boolean includeHidden) {
        for (Entry<L2PcInstance, Boolean> entry : _gmList.entrySet()) {
            if (includeHidden || !entry.getValue()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Send list to player.
     *
     * @param player the player
     */
    public void sendListToPlayer(L2PcInstance player) {
        if (isGmOnline(player.isGM())) {
            player.sendPacket(SystemMessageId.GM_LIST);

            for (String name : getAllGmNames(player.isGM())) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.GM_C1);
                sm.addString(name);
                player.sendPacket(sm);
            }
        } else {
            player.sendPacket(SystemMessageId.THERE_ARE_NO_GMS_CURRENTLY_VISIBLE_IN_THE_PUBLIC_LIST_AS_THEY_MAY_BE_PERFORMING_OTHER_FUNCTIONS_AT_THE_MOMENT);
        }
    }

    /**
     * Broadcast to GMs.
     *
     * @param packet the packet
     */
    public void broadcastToGMs(IClientOutgoingPacket packet) {
        for (L2PcInstance gm : getAllGms(true)) {
            gm.sendPacket(packet);
        }
    }

    /**
     * Broadcast message to GMs.
     *
     * @param message the message
     * @return the message that was broadcasted
     */
    public String broadcastMessageToGMs(String message) {
        for (L2PcInstance gm : getAllGms(true)) {
            gm.sendMessage(message);
        }
        return message;
    }

    private static class SingletonHolder {
        protected static final AdminData _instance = new AdminData();
    }
}
