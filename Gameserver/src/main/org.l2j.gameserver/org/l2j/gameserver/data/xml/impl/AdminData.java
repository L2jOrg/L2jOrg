package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.model.L2AccessLevel;
import org.l2j.gameserver.model.L2AdminCommandAccessRight;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.util.IGameXmlReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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


/**
 * Loads administrator access levels and commands.
 *
 * @author UnAfraid
 */
public final class AdminData extends IGameXmlReader{
    private static final Logger LOGGER = LoggerFactory.getLogger(AdminData.class);

    private final Map<Integer, L2AccessLevel> _accessLevels = new HashMap<>();
    private final Map<String, L2AdminCommandAccessRight> _adminCommandAccessRights = new HashMap<>();
    private final Map<L2PcInstance, Boolean> _gmList = new ConcurrentHashMap<>();
    private int _highestLevel = 0;

    private AdminData() {
        load();
    }

    @Override
    public synchronized void load() {
        _accessLevels.clear();
        _adminCommandAccessRights.clear();
        parseFile(new File("config/AccessLevels.xml"));
        LOGGER.info(getClass().getSimpleName() + ": Loaded: " + _accessLevels.size() + " Access Levels.");
        parseFile(new File("config/AdminCommands.xml"));
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

    public static AdminData getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final AdminData INSTANCE = new AdminData();
    }
}
