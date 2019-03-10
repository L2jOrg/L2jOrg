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

import com.l2jmobius.Config;
import org.l2j.commons.util.IGameXmlReader;
import org.l2j.gameserver.mobius.gameserver.datatables.ItemTable;
import org.l2j.gameserver.mobius.gameserver.model.StatsSet;
import org.l2j.gameserver.mobius.gameserver.model.holders.ItemHolder;
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
