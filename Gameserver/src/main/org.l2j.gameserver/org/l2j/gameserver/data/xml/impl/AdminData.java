/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.model.AccessLevel;
import org.l2j.gameserver.model.AdminCommandAccessRight;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.settings.GeneralSettings;
import org.l2j.gameserver.util.GameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import static java.util.Objects.isNull;
import static org.l2j.commons.configuration.Configurator.getSettings;


/**
 * Loads administrator access levels and commands.
 *
 * @author UnAfraid
 */
public final class AdminData extends GameXmlReader {
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminData.class);

    private final Map<Integer, AccessLevel> _accessLevels = new HashMap<>();
    private final Map<String, AdminCommandAccessRight> _adminCommandAccessRights = new HashMap<>();
    private final Map<Player, Boolean> _gmList = new ConcurrentHashMap<>();
    private int _highestLevel = 0;

    private AdminData() {
        load();
    }

    @Override
    protected Path getSchemaFilePath() {
        return Path.of("config/xsd/AccessLevels.xsd");
    }

    @Override
    public synchronized void load() {
        _accessLevels.clear();
        _adminCommandAccessRights.clear();
        parseFile(new File("config/AccessLevels.xml"));
        LOGGER.info("Loaded: {} Access Levels.", _accessLevels.size());
        LOGGER.info("Loaded: {} Access Commands.", _adminCommandAccessRights.size());
        releaseResources();
    }

    @Override
    public void parseDocument(Document doc, File f) {
        NamedNodeMap attrs;
        Node attr;
        StatsSet set;
        AccessLevel level;
        AdminCommandAccessRight command;
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
                        level = new AccessLevel(set);
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
                        command = new AdminCommandAccessRight(set);
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
    public AccessLevel getAccessLevel(int accessLevelNum) {
        if (accessLevelNum < 0) {
            return _accessLevels.get(-1);
        }
        return _accessLevels.get(accessLevelNum);
    }

    public AccessLevel getAccessLevelOrDefault(int level) {
        AccessLevel accessLevel = getAccessLevel(level);

        if (isNull(accessLevel)) {
            LOGGER.warn("Can't find access level {}", level);
            accessLevel = AdminData.getInstance().getAccessLevel(0);
        }

        var generalSettings = getSettings(GeneralSettings.class);
        var defaultAccessLevel = generalSettings.defaultAccessLevel();

        if (accessLevel.getLevel() == 0 && defaultAccessLevel > 0) {
            accessLevel = AdminData.getInstance().getAccessLevel(defaultAccessLevel);
            if (isNull(accessLevel)) {
                LOGGER.warn("Config's default access level ({}) is not defined, defaulting to 0!", defaultAccessLevel);
                accessLevel = AdminData.getInstance().getAccessLevel(0);
                generalSettings.setDefaultAccessLevel(0);
            }
        }
        return  accessLevel;
    }


    /**
     * Gets the master access level.
     *
     * @return the master access level
     */
    public AccessLevel getMasterAccessLevel() {
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
    public boolean hasAccess(String adminCommand, AccessLevel accessLevel) {
        AdminCommandAccessRight acar = _adminCommandAccessRights.get(adminCommand);
        if (isNull(acar)) {
            if (accessLevel.getLevel() < _highestLevel) {
                LOGGER.info("No rights defined for admin command {}!", adminCommand);
                return false;
            }

            acar = new AdminCommandAccessRight(adminCommand, true, accessLevel.getLevel());
            _adminCommandAccessRights.put(adminCommand, acar);
            LOGGER.info("No rights defined for admin command '{}' auto setting accesslevel: '{}'!", adminCommand, accessLevel.getLevel());
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
        final AdminCommandAccessRight acar = _adminCommandAccessRights.get(command);
        if (acar == null) {
            LOGGER.info("No rights defined for admin command '{}'.", command);
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
    public List<Player> getAllGms(boolean includeHidden) {
        final List<Player> tmpGmList = new ArrayList<>();
        for (Entry<Player, Boolean> entry : _gmList.entrySet()) {
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
        for (Entry<Player, Boolean> entry : _gmList.entrySet()) {
            if (!entry.getValue()) {
                tmpGmList.add(entry.getKey().getName());
            } else if (includeHidden) {
                tmpGmList.add(entry.getKey().getName() + " (invis)");
            }
        }
        return tmpGmList;
    }

    /**
     * Add a Player player to the Set _gmList.
     *
     * @param player the player
     * @param hidden the hidden
     */
    public void addGm(Player player, boolean hidden) {
        _gmList.put(player, hidden);
    }

    /**
     * Delete a GM.
     *
     * @param player the player
     */
    public void deleteGm(Player player) {
        _gmList.remove(player);
    }

    /**
     * GM will be displayed on clients GM list.
     *
     * @param player the player
     */
    public void showGm(Player player) {
        _gmList.putIfAbsent(player, false);
    }

    /**
     * GM will no longer be displayed on clients GM list.
     *
     * @param player the player
     */
    public void hideGm(Player player) {
        _gmList.putIfAbsent(player, true);
    }

    /**
     * Checks if is GM online.
     *
     * @param includeHidden the include hidden
     * @return true, if is GM online
     */
    public boolean isGmOnline(boolean includeHidden) {
        for (Entry<Player, Boolean> entry : _gmList.entrySet()) {
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
    public void sendListToPlayer(Player player) {
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
    public void broadcastToGMs(ServerPacket packet) {
        for (Player gm : getAllGms(true)) {
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
        for (Player gm : getAllGms(true)) {
            gm.sendMessage(message);
        }
        return message;
    }

    public static AdminData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final AdminData INSTANCE = new AdminData();
    }
}
