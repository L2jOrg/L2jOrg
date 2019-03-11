package org.l2j.gameserver.model;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.data.sql.impl.CharNameTable;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.BlockListPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BlockList {
    private static final Map<Integer, List<Integer>> OFFLINE_LIST = new ConcurrentHashMap<>();
    private static Logger LOGGER = Logger.getLogger(BlockList.class.getName());
    private final L2PcInstance _owner;
    private List<Integer> _blockList;

    public BlockList(L2PcInstance owner) {
        _owner = owner;
        _blockList = OFFLINE_LIST.get(owner.getObjectId());
        if (_blockList == null) {
            _blockList = loadList(_owner.getObjectId());
        }
    }

    private static List<Integer> loadList(int ObjId) {
        final List<Integer> list = new ArrayList<>();
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("SELECT friendId FROM character_friends WHERE charId=? AND relation=1")) {
            statement.setInt(1, ObjId);
            try (ResultSet rset = statement.executeQuery()) {
                int friendId;
                while (rset.next()) {
                    friendId = rset.getInt("friendId");
                    if (friendId == ObjId) {
                        continue;
                    }
                    list.add(friendId);
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error found in " + ObjId + " FriendList while loading BlockList: " + e.getMessage(), e);
        }
        return list;
    }

    public static boolean isBlocked(L2PcInstance listOwner, L2PcInstance target) {
        final BlockList blockList = listOwner.getBlockList();
        return blockList.isBlockAll() || blockList.isInBlockList(target);
    }

    public static boolean isBlocked(L2PcInstance listOwner, int targetId) {
        final BlockList blockList = listOwner.getBlockList();
        return blockList.isBlockAll() || blockList.isInBlockList(targetId);
    }

    public static void addToBlockList(L2PcInstance listOwner, int targetId) {
        if (listOwner == null) {
            return;
        }

        final String charName = CharNameTable.getInstance().getNameById(targetId);

        if (listOwner.getFriendList().contains(targetId)) {
            listOwner.sendPacket(SystemMessageId.THIS_PLAYER_IS_ALREADY_REGISTERED_ON_YOUR_FRIENDS_LIST);
            return;
        }

        if (listOwner.getBlockList().getBlockList().contains(targetId)) {
            listOwner.sendMessage("Already in ignore list.");
            return;
        }

        listOwner.getBlockList().addToBlockList(targetId);

        SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_ADDED_TO_YOUR_IGNORE_LIST);
        sm.addString(charName);
        listOwner.sendPacket(sm);

        final L2PcInstance player = L2World.getInstance().getPlayer(targetId);

        if (player != null) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_PLACED_YOU_ON_HIS_HER_IGNORE_LIST);
            sm.addString(listOwner.getName());
            player.sendPacket(sm);
        }
    }

    public static void removeFromBlockList(L2PcInstance listOwner, int targetId) {
        if (listOwner == null) {
            return;
        }

        SystemMessage sm;

        final String charName = CharNameTable.getInstance().getNameById(targetId);

        if (!listOwner.getBlockList().getBlockList().contains(targetId)) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.THAT_IS_AN_INCORRECT_TARGET);
            listOwner.sendPacket(sm);
            return;
        }

        listOwner.getBlockList().removeFromBlockList(targetId);

        sm = SystemMessage.getSystemMessage(SystemMessageId.S1_HAS_BEEN_REMOVED_FROM_YOUR_IGNORE_LIST);
        sm.addString(charName);
        listOwner.sendPacket(sm);
    }

    public static boolean isInBlockList(L2PcInstance listOwner, L2PcInstance target) {
        return listOwner.getBlockList().isInBlockList(target);
    }

    public static void setBlockAll(L2PcInstance listOwner, boolean newValue) {
        listOwner.getBlockList().setBlockAll(newValue);
    }

    public static void sendListToOwner(L2PcInstance listOwner) {
        listOwner.sendPacket(new BlockListPacket(listOwner.getBlockList().getBlockList()));
    }

    /**
     * @param ownerId  object id of owner block list
     * @param targetId object id of potential blocked player
     * @return true if blocked
     */
    public static boolean isInBlockList(int ownerId, int targetId) {
        final L2PcInstance player = L2World.getInstance().getPlayer(ownerId);
        if (player != null) {
            return isBlocked(player, targetId);
        }
        if (!OFFLINE_LIST.containsKey(ownerId)) {
            OFFLINE_LIST.put(ownerId, loadList(ownerId));
        }
        return OFFLINE_LIST.get(ownerId).contains(targetId);
    }

    private void addToBlockList(int target) {
        _blockList.add(target);
        updateInDB(target, true);
    }

    private void removeFromBlockList(int target) {
        _blockList.remove(Integer.valueOf(target));
        updateInDB(target, false);
    }

    public void playerLogout() {
        OFFLINE_LIST.put(_owner.getObjectId(), _blockList);
    }

    private void updateInDB(int targetId, boolean state) {
        try (Connection con = DatabaseFactory.getInstance().getConnection()) {
            if (state) // add
            {
                try (PreparedStatement statement = con.prepareStatement("INSERT INTO character_friends (charId, friendId, relation) VALUES (?, ?, 1)")) {
                    statement.setInt(1, _owner.getObjectId());
                    statement.setInt(2, targetId);
                    statement.execute();
                }
            } else
            // remove
            {
                try (PreparedStatement statement = con.prepareStatement("DELETE FROM character_friends WHERE charId=? AND friendId=? AND relation=1")) {
                    statement.setInt(1, _owner.getObjectId());
                    statement.setInt(2, targetId);
                    statement.execute();
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Could not add block player: " + e.getMessage(), e);
        }
    }

    public boolean isInBlockList(L2PcInstance target) {
        return _blockList.contains(target.getObjectId());
    }

    public boolean isInBlockList(int targetId) {
        return _blockList.contains(targetId);
    }

    public boolean isBlockAll() {
        return _owner.getMessageRefusal();
    }

    private void setBlockAll(boolean state) {
        _owner.setMessageRefusal(state);
    }

    private List<Integer> getBlockList() {
        return _blockList;
    }

    public boolean isBlockAll(L2PcInstance listOwner) {
        return listOwner.getBlockList().isBlockAll();
    }
}
