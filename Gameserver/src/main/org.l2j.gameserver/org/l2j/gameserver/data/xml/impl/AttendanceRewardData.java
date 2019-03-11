package org.l2j.gameserver.data.xml.impl;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.util.IGameXmlReader;
import org.w3c.dom.Document;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * @author Mobius
 */
public class AttendanceRewardData implements IGameXmlReader {
    private static Logger LOGGER = Logger.getLogger(AttendanceRewardData.class.getName());
    private final List<ItemHolder> _rewards = new ArrayList<>();
    private int _rewardsCount = 0;

    protected AttendanceRewardData() {
        load();
    }

    public static AttendanceRewardData getInstance() {
        return SingletonHolder._instance;
    }

    @Override
    public void load() {
        if (Config.ENABLE_ATTENDANCE_REWARDS) {
            _rewards.clear();
            parseDatapackFile("data/AttendanceRewards.xml");
            _rewardsCount = _rewards.size();
            LOGGER.info(getClass().getSimpleName() + ": Loaded " + _rewardsCount + " rewards.");
        } else {
            LOGGER.info(getClass().getSimpleName() + ": Disabled.");
        }
    }

    @Override
    public void parseDocument(Document doc, File f) {
        forEach(doc, "list", listNode -> forEach(listNode, "item", rewardNode ->
        {
            final StatsSet set = new StatsSet(parseAttributes(rewardNode));
            final int itemId = set.getInt("id");
            final int itemCount = set.getInt("count");
            if (ItemTable.getInstance().getTemplate(itemId) == null) {
                LOGGER.info(getClass().getSimpleName() + ": Item with id " + itemId + " does not exist.");
            } else {
                _rewards.add(new ItemHolder(itemId, itemCount));
            }
        }));
    }

    public List<ItemHolder> getRewards() {
        return _rewards;
    }

    public int getRewardsCount() {
        return _rewardsCount;
    }

    private static class SingletonHolder {
        protected static final AttendanceRewardData _instance = new AttendanceRewardData();
    }
}
