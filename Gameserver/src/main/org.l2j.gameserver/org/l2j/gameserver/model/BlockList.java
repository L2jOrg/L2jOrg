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
package org.l2j.gameserver.model;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.data.database.dao.PlayerDAO;
import org.l2j.gameserver.data.sql.impl.PlayerNameTable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.BlockListPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.world.World;

import static org.l2j.commons.database.DatabaseAccess.getDAO;

/**
 * @author JoeAlisson
 */
public class BlockList {
    private static final IntMap<IntSet> OFFLINE_LIST = new CHashIntMap<>();
    private final Player owner;
    private IntSet list;

    public BlockList(Player owner) {
        this.owner = owner;
        list = OFFLINE_LIST.get(owner.getObjectId());
        if (list == null) {
            list = loadList(this.owner.getObjectId());
        }
    }

    private void addToBlockList(int target) {
        list.add(target);
        updateInDB(target, true);
    }

    private void removeFromBlockList(int target) {
        list.remove(target);
        updateInDB(target, false);
    }

    public void playerLogout() {
        OFFLINE_LIST.put(owner.getObjectId(), list);
    }

    private void updateInDB(int targetId, boolean add) {
        if (add) {
            getDAO(PlayerDAO.class).saveBlockedPlayer(owner.getObjectId(), targetId);
        } else {
            getDAO(PlayerDAO.class).deleteBlockedPlayer(owner.getObjectId(), targetId);
        }
    }

    public boolean isInBlockList(Player target) {
        return list.contains(target.getObjectId());
    }

    public boolean isInBlockList(int targetId) {
        return list.contains(targetId);
    }

    public boolean isBlockAll() {
        return owner.isMessageRefusing();
    }

    private void setBlockAll(boolean state) {
        owner.setMessageRefusing(state);
    }

    private IntSet getBlockList() {
        return list;
    }

    private static IntSet loadList(int objId) {
        return getDAO(PlayerDAO.class).findBlockListById(objId);
    }

    public static boolean isBlocked(Player listOwner, Player target) {
        final BlockList blockList = listOwner.getBlockList();
        return blockList.isBlockAll() || blockList.isInBlockList(target);
    }

    public static boolean isBlocked(Player listOwner, int targetId) {
        final BlockList blockList = listOwner.getBlockList();
        return blockList.isBlockAll() || blockList.isInBlockList(targetId);
    }

    public static void addToBlockList(Player listOwner, int targetId) {
        if (listOwner == null) {
            return;
        }

        final String charName = PlayerNameTable.getInstance().getNameById(targetId);

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

        final Player player = World.getInstance().findPlayer(targetId);

        if (player != null) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.C1_HAS_PLACED_YOU_ON_HIS_HER_IGNORE_LIST);
            sm.addString(listOwner.getName());
            player.sendPacket(sm);
        }
    }

    public static void removeFromBlockList(Player listOwner, int targetId) {
        if (listOwner == null) {
            return;
        }

        SystemMessage sm;

        final String charName = PlayerNameTable.getInstance().getNameById(targetId);

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

    public static void setBlockAll(Player listOwner, boolean newValue) {
        listOwner.getBlockList().setBlockAll(newValue);
    }

    public static void sendListToOwner(Player listOwner) {
        listOwner.sendPacket(new BlockListPacket(listOwner.getBlockList().getBlockList()));
    }

    /**
     * @param ownerId  object id of owner block list
     * @param targetId object id of potential blocked player
     * @return true if blocked
     */
    public static boolean isInBlockList(int ownerId, int targetId) {
        final Player player = World.getInstance().findPlayer(ownerId);
        if (player != null) {
            return isBlocked(player, targetId);
        }
        if (!OFFLINE_LIST.containsKey(ownerId)) {
            OFFLINE_LIST.put(ownerId, loadList(ownerId));
        }
        return OFFLINE_LIST.get(ownerId).contains(targetId);
    }

}
