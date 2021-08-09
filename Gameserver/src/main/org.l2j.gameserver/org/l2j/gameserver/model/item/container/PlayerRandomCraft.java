package org.l2j.gameserver.model.item.container;


import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.database.dao.RandomCraftDAO;
import org.l2j.gameserver.data.database.data.RandomCraftDAOData;
import org.l2j.gameserver.data.xml.RandomCraftData;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.RandomCraftRequest;
import org.l2j.gameserver.model.holders.RandomCraftRewardItemHolder;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.item.ExItemAnnounce;
import org.l2j.gameserver.network.serverpackets.randomcraft.ExCraftInfo;
import org.l2j.gameserver.network.serverpackets.randomcraft.ExCraftRandomInfo;
import org.l2j.gameserver.network.serverpackets.randomcraft.ExCraftRandomMake;
import org.l2j.gameserver.network.serverpackets.randomcraft.ExCraftRandomRefresh;
import org.l2j.gameserver.util.Broadcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;

import static org.l2j.commons.database.DatabaseAccess.getDAO;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.network.serverpackets.item.ItemAnnounceType.RANDOM_CRAFT;

public class PlayerRandomCraft {
    private static final Logger LOGGER = LoggerFactory.getLogger(PlayerRandomCraft.class);

    public static final int MAX_FULL_CRAFT_POINTS = 99;
    public static final int MAX_CRAFT_POINTS = 1000000;

    private final Player _player;
    private final List<RandomCraftRewardItemHolder> _rewardList = new ArrayList<>(5);

    private int _fullCraftPoints = 0;
    private int _craftPoints = 0;
    private boolean _isSayhaRoll = false;

    public PlayerRandomCraft(Player player)
    {
        _player = player;
    }

    public void restore()
    {
        RandomCraftDAO dao = getDAO(RandomCraftDAO.class);
        RandomCraftDAOData data = dao.findByCharId(_player.getObjectId());
        if (data != null) {
            _fullCraftPoints = data.getRandomCraftFullPoints();

            _craftPoints = data.getRandomCraftPoints();
            _isSayhaRoll = data.getSayhaRoll();

            _rewardList.add(0, new RandomCraftRewardItemHolder(data.getItem1Id(), data.getItem1Count(), data.getItem1Locked(), data.getItem1LockLeft()));
            _rewardList.add(1, new RandomCraftRewardItemHolder(data.getItem2Id(), data.getItem2Count(), data.getItem2Locked(), data.getItem2LockLeft()));
            _rewardList.add(2, new RandomCraftRewardItemHolder(data.getItem3Id(), data.getItem3Count(), data.getItem3Locked(), data.getItem3LockLeft()));
            _rewardList.add(3, new RandomCraftRewardItemHolder(data.getItem4Id(), data.getItem4Count(), data.getItem4Locked(), data.getItem4LockLeft()));
            _rewardList.add(4, new RandomCraftRewardItemHolder(data.getItem5Id(), data.getItem5Count(), data.getItem5Locked(), data.getItem5LockLeft()));
        } else {
            dao.storeNew(_player.getObjectId(), _fullCraftPoints, _craftPoints, _isSayhaRoll, 0,0,false,0, 0,0,false,0,0,0,false,0,0,0,false,0,0,0,false,0);
        }
    }

    public void store()
    {
        RandomCraftDAO dao = getDAO(RandomCraftDAO.class);
        final int item_1_id = _rewardList.size() >= 1 ? _rewardList.get(0) != null ? _rewardList.get(0).getItemId() : 0 : 0;
        final long item_1_count = _rewardList.size() >= 1 ? _rewardList.get(0) != null ? _rewardList.get(0).getItemCount() : 0 : 0;
        final boolean item_1_locked = _rewardList.size() >= 1 ? _rewardList.get(0) != null ? _rewardList.get(0).isLocked() : false : false;
        final int item_1_lock_left = _rewardList.size() >= 1 ? _rewardList.get(0) != null ? _rewardList.get(0).getLockLeft() : 20 : 20;
        final int item_2_id = _rewardList.size() >= 2 ? _rewardList.get(1) != null ? _rewardList.get(1).getItemId() : 0 : 0;
        final long item_2_count = _rewardList.size() >= 2 ? _rewardList.get(1) != null ? _rewardList.get(1).getItemCount() : 0 : 0;
        final boolean item_2_locked = _rewardList.size() >= 2 ? _rewardList.get(1) != null ? _rewardList.get(1).isLocked() : false : false;
        final int item_2_lock_left = _rewardList.size() >= 2 ? _rewardList.get(1) != null ? _rewardList.get(1).getLockLeft() : 20 : 20;
        final int item_3_id = _rewardList.size() >= 3 ? _rewardList.get(2) != null ? _rewardList.get(2).getItemId() : 0 : 0;
        final long item_3_count = _rewardList.size() >= 3 ? _rewardList.get(2) != null ? _rewardList.get(2).getItemCount() : 0 : 0;
        final boolean item_3_locked = _rewardList.size() >= 3 ? _rewardList.get(2) != null ? _rewardList.get(2).isLocked() : false : false;
        final int item_3_lock_left = _rewardList.size() >= 3 ? _rewardList.get(2) != null ? _rewardList.get(2).getLockLeft() : 20 : 20;
        final int item_4_id = _rewardList.size() >= 4 ? _rewardList.get(3) != null ? _rewardList.get(3).getItemId() : 0 : 0;
        final long item_4_count = _rewardList.size() >= 4 ? _rewardList.get(3) != null ? _rewardList.get(3).getItemCount() : 0 : 0;
        final boolean item_4_locked = _rewardList.size() >= 4 ? _rewardList.get(3) != null ? _rewardList.get(3).isLocked() : false : false;
        final int item_4_lock_left = _rewardList.size() >= 4 ? _rewardList.get(3) != null ? _rewardList.get(3).getLockLeft() : 20 : 20;
        final int item_5_id = _rewardList.size() == 5 ? _rewardList.get(4) != null ? _rewardList.get(4).getItemId() : 0 : 0;
        final long item_5_count = _rewardList.size() == 5 ? _rewardList.get(4) != null ? _rewardList.get(4).getItemCount() : 0 : 0;
        final boolean item_5_locked = _rewardList.size() == 5 ? _rewardList.get(4) != null ? _rewardList.get(4).isLocked() : false : false;
        final int item_5_lock_left = _rewardList.size() == 5 ? _rewardList.get(4) != null ? _rewardList.get(4).getLockLeft() : 20 : 20;
        dao.updateRandomCraft(_player.getObjectId(), _fullCraftPoints, _craftPoints, _isSayhaRoll, item_1_id, item_1_count, item_1_locked, item_1_lock_left, item_2_id, item_2_count, item_2_locked, item_2_lock_left, item_3_id,item_3_count, item_3_locked,item_3_lock_left,item_4_id,item_4_count,item_4_locked,item_4_lock_left,item_5_id,item_5_count,item_5_locked,item_5_lock_left);
    }

    public void refresh()
    {
        if (_player.hasItemRequest() || _player.hasRequest(RandomCraftRequest.class))
        {
            return;
        }
        _player.addRequest(new RandomCraftRequest(_player));

        if ((_fullCraftPoints > 0) && _player.reduceAdena("RandomCraft Refresh", 10000, _player, true))
        {
            _player.sendPacket(new ExCraftInfo(_player));
            _player.sendPacket(new ExCraftRandomRefresh());
            _fullCraftPoints--;
            if (_isSayhaRoll)
            {
                _player.addItem("RandomCraft Roll", 91641, 2, _player, true);
                _isSayhaRoll = false;
            }
            _player.sendPacket(new ExCraftInfo(_player));

            for (int i = 0; i < 5; i++)
            {
                final RandomCraftRewardItemHolder holder;
                if (i > (_rewardList.size() - 1))
                {
                    holder = null;
                }
                else
                {
                    holder = _rewardList.get(i);
                }

                if (holder == null)
                {
                    _rewardList.add(i, getNewReward());
                }
                else if (!holder.isLocked())
                {
                    _rewardList.set(i, getNewReward());
                }
                else
                {
                    holder.decLock();
                }
            }
            _player.sendPacket(new ExCraftRandomInfo(_player));
        }

        _player.removeRequest(RandomCraftRequest.class);
    }

    private RandomCraftRewardItemHolder getNewReward()
    {
        if (RandomCraftData.getInstance().isEmpty())
        {
            return null;
        }

        RandomCraftRewardItemHolder result = null;
        while (result == null)
        {
            result = RandomCraftData.getInstance().getNewReward();
            SEARCH: for (RandomCraftRewardItemHolder reward : _rewardList)
            {
                if (reward.getItemId() == result.getItemId())
                {
                    result = null;
                    break SEARCH;
                }
            }
        }
        return result;
    }

    public void make()
    {
        if (_player.hasItemRequest() || _player.hasRequest(RandomCraftRequest.class))
        {
            return;
        }
        _player.addRequest(new RandomCraftRequest(_player));

        if (_player.reduceAdena("RandomCraft Make", 500000, _player, true))
        {
            final int madeId = Rnd.get(0, 4);
            final RandomCraftRewardItemHolder holder = _rewardList.get(madeId);
            final int itemId = holder.getItemId();
            final long itemCount = holder.getItemCount();
            _rewardList.clear();
            final Item item = _player.addItem("RandomCraft Make", itemId, itemCount, _player, true);
            if (RandomCraftData.getInstance().isAnnounce(itemId))
            {
                Broadcast.toAllOnlinePlayers(new ExItemAnnounce(RANDOM_CRAFT, _player, item),
                        getSystemMessage(SystemMessageId.S1_HAS_OBTAINED_AN_ITEM_USING_RANDOM_CRAFT));
                LOGGER.info(_player + " randomly crafted " + item + " [" + item.getObjectId() + "]");
            }
            _player.sendPacket(new ExCraftRandomMake(itemId, itemCount));
            _player.sendPacket(new ExCraftRandomInfo(_player));

        }
        _player.removeRequest(RandomCraftRequest.class);
    }

    public List<RandomCraftRewardItemHolder> getRewards()
    {
        return _rewardList;
    }

    public int getFullCraftPoints()
    {
        return _fullCraftPoints;
    }

    public void addFullCraftPoints(int value)
    {
        addFullCraftPoints(value, false);
    }

    public void addFullCraftPoints(int value, boolean broadcast)
    {
        _fullCraftPoints = Math.min(_fullCraftPoints + value, MAX_FULL_CRAFT_POINTS);
        if (_craftPoints >= MAX_CRAFT_POINTS)
        {
            _craftPoints = 0;
        }
        if (value > 0)
        {
            _isSayhaRoll = true;
        }
        if (broadcast)
        {
            _player.sendPacket(new ExCraftInfo(_player));
        }
    }

    public void removeFullCraftPoints(int value)
    {
        _fullCraftPoints -= value;
        _player.sendPacket(new ExCraftInfo(_player));
    }

    public void addCraftPoints(int value)
    {
        if ((_craftPoints - 1) < MAX_CRAFT_POINTS)
        {
            _craftPoints += value;
        }

        final int fullPointsToAdd = _craftPoints / MAX_CRAFT_POINTS;
        final int pointsToRemove = MAX_CRAFT_POINTS * fullPointsToAdd;

        _craftPoints -= pointsToRemove;
        addFullCraftPoints(fullPointsToAdd);
        if (_fullCraftPoints == MAX_FULL_CRAFT_POINTS)
        {
            _craftPoints = MAX_CRAFT_POINTS;
        }

        _player.sendPacket(SystemMessageId.YOU_HAVE_ACQUIRED_S1_CRAFT_SCALE_POINTS);
        _player.sendPacket(new ExCraftInfo(_player));
    }

    public int getCraftPoints()
    {
        return _craftPoints;
    }

    public void setIsSayhaRoll(boolean value)
    {
        _isSayhaRoll = value;
    }

    public boolean isSayhaRoll()
    {
        return _isSayhaRoll;
    }

    public int getLockedSlotCount()
    {
        int count = 0;
        for (RandomCraftRewardItemHolder holder : _rewardList)
        {
            if (holder.isLocked())
            {
                count++;
            }
        }
        return count;
    }
}
