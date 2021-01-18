package org.l2j.gameserver.model.item.container;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.RandomCraftData;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.RandomCraftRequest;
import org.l2j.gameserver.model.holders.RandomCraftRewardItemHolder;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.item.ExItemAnnounce;
import org.l2j.gameserver.network.serverpackets.item.ItemAnnounceType;
import org.l2j.gameserver.network.serverpackets.randomcraft.ExCraftInfo;
import org.l2j.gameserver.network.serverpackets.randomcraft.ExCraftRandomInfo;
import org.l2j.gameserver.network.serverpackets.randomcraft.ExCraftRandomMake;
import org.l2j.gameserver.network.serverpackets.randomcraft.ExCraftRandomRefresh;
import org.l2j.gameserver.util.Broadcast;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

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
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM character_random_craft WHERE charId=?"))
        {
            ps.setInt(1, _player.getObjectId());
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    try
                    {
                        _fullCraftPoints = rs.getInt("random_craft_full_points");
                        _craftPoints = rs.getInt("random_craft_points");
                        _isSayhaRoll = rs.getBoolean("sayha_roll");
                        for (int i = 1; i <= 5; i++)
                        {
                            final int itemId = rs.getInt("item_" + i + "_id");
                            final long itemCount = rs.getLong("item_" + i + "_count");
                            final boolean itemLocked = rs.getBoolean("item_" + i + "_locked");
                            final int itemLockLeft = rs.getInt("item_" + i + "_lock_left");
                            final RandomCraftRewardItemHolder holder = new RandomCraftRewardItemHolder(itemId, itemCount, itemLocked, itemLockLeft);
                            _rewardList.add(i - 1, holder);
                        }
                    }
                    catch (Exception e)
                    {
                        LOGGER.info("Could not restore random craft for " + _player);
                    }
                }
                else
                {
                    storeNew();
                }
            }
        }
        catch (Exception e)
        {
            LOGGER.info("Could not restore random craft for " + _player, e);
        }
    }

    public void store()
    {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE character_random_craft SET random_craft_full_points=?,random_craft_points=?,sayha_roll=?,item_1_id=?,item_1_count=?,item_1_locked=?,item_1_lock_left=?,item_2_id=?,item_2_count=?,item_2_locked=?,item_2_lock_left=?,item_3_id=?,item_3_count=?,item_3_locked=?,item_3_lock_left=?,item_4_id=?,item_4_count=?,item_4_locked=?,item_4_lock_left=?,item_5_id=?,item_5_count=?,item_5_locked=?,item_5_lock_left=?"))
        {
            ps.setInt(1, _fullCraftPoints);
            ps.setInt(2, _craftPoints);
            ps.setBoolean(3, _isSayhaRoll);
            for (int i = 0; i < 5; i++)
            {
                if (_rewardList.size() >= (i + 1))
                {
                    final RandomCraftRewardItemHolder holder = _rewardList.get(i);
                    ps.setInt(4 + (i * 4), holder == null ? 0 : holder.getItemId());
                    ps.setLong(5 + (i * 4), holder == null ? 0 : holder.getItemCount());
                    ps.setBoolean(6 + (i * 4), holder == null ? false : holder.isLocked());
                    ps.setInt(7 + (i * 4), holder == null ? 20 : holder.getLockLeft());
                }
                else
                {
                    ps.setInt(4 + (i * 4), 0);
                    ps.setLong(5 + (i * 4), 0);
                    ps.setBoolean(6 + (i * 4), false);
                    ps.setInt(7 + (i * 4), 20);
                }
            }
            ps.execute();
        }
        catch (Exception e)
        {
            LOGGER.info( "Could not store RandomCraft for: " + _player, e);
        }
    }

    public void storeNew()
    {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO character_random_craft VALUES(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"))
        {
            ps.setInt(1, _player.getObjectId());
            ps.setInt(2, _fullCraftPoints);
            ps.setInt(3, _craftPoints);
            ps.setBoolean(4, _isSayhaRoll);
            for (int i = 0; i < 5; i++)
            {
                ps.setInt(5 + (i * 4), 0);
                ps.setLong(6 + (i * 4), 0);
                ps.setBoolean(7 + (i * 4), false);
                ps.setInt(8 + (i * 4), 0);
            }
            ps.executeUpdate();
        }
        catch (Exception e)
        {
            LOGGER.info("Could not store new RandomCraft for: " + _player, e.getMessage());
        }
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
