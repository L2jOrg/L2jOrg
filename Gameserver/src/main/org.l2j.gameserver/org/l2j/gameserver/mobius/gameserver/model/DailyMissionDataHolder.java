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
package org.l2j.gameserver.mobius.gameserver.model;

import org.l2j.gameserver.mobius.gameserver.enums.DailyMissionStatus;
import org.l2j.gameserver.mobius.gameserver.handler.AbstractDailyMissionHandler;
import org.l2j.gameserver.mobius.gameserver.handler.DailyMissionHandler;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.base.ClassId;
import org.l2j.gameserver.mobius.gameserver.model.holders.ItemHolder;

import java.util.List;
import java.util.function.Function;

/**
 * @author Sdw
 */
public class DailyMissionDataHolder {
    private final int _id;
    private final int _rewardId;
    private final List<ItemHolder> _rewardsItems;
    private final List<ClassId> _classRestriction;
    private final int _requiredCompletions;
    private final StatsSet _params;
    private final boolean _isOneTime;
    private final boolean _isMainClassOnly;
    private final boolean _isDualClassOnly;
    private final boolean _isDisplayedWhenNotAvailable;
    private final AbstractDailyMissionHandler _handler;

    public DailyMissionDataHolder(StatsSet set) {
        final Function<DailyMissionDataHolder, AbstractDailyMissionHandler> handler = DailyMissionHandler.getInstance().getHandler(set.getString("handler"));

        _id = set.getInt("id");
        _rewardId = set.getInt("reward_id");
        _requiredCompletions = set.getInt("requiredCompletion", 0);
        _rewardsItems = set.getList("items", ItemHolder.class);
        _classRestriction = set.getList("classRestriction", ClassId.class);
        _params = set.getObject("params", StatsSet.class);
        _isOneTime = set.getBoolean("isOneTime", true);
        _isMainClassOnly = set.getBoolean("isMainClassOnly", true);
        _isDualClassOnly = set.getBoolean("isDualClassOnly", false);
        _isDisplayedWhenNotAvailable = set.getBoolean("isDisplayedWhenNotAvailable", true);
        _handler = handler != null ? handler.apply(this) : null;
    }

    public int getId() {
        return _id;
    }

    public int getRewardId() {
        return _rewardId;
    }

    public List<ClassId> getClassRestriction() {
        return _classRestriction;
    }

    public List<ItemHolder> getRewards() {
        return _rewardsItems;
    }

    public int getRequiredCompletions() {
        return _requiredCompletions;
    }

    public StatsSet getParams() {
        return _params;
    }

    public boolean isOneTime() {
        return _isOneTime;
    }

    public boolean isMainClassOnly() {
        return _isMainClassOnly;
    }

    public boolean isDualClassOnly() {
        return _isDualClassOnly;
    }

    public boolean isDisplayedWhenNotAvailable() {
        return _isDisplayedWhenNotAvailable;
    }

    public boolean isDisplayable(L2PcInstance player) {
        // Check if its main class only
        if (isMainClassOnly() && (player.isSubClassActive() || player.isDualClassActive())) {
            return false;
        }

        // Check if its dual class only.
        if (isDualClassOnly() && !player.isDualClassActive()) {
            return false;
        }

        // Check for specific class restrictions
        if (!_classRestriction.isEmpty() && !_classRestriction.contains(player.getClassId())) {
            return false;
        }

        final int status = getStatus(player);
        if (!isDisplayedWhenNotAvailable() && (status == DailyMissionStatus.NOT_AVAILABLE.getClientId())) {
            return false;
        }

        // Show only if its repeatable, recently completed or incompleted that has met the checks above.
        return (!isOneTime() || getRecentlyCompleted(player) || (status != DailyMissionStatus.COMPLETED.getClientId()));
    }

    public void requestReward(L2PcInstance player) {
        if ((_handler != null) && isDisplayable(player)) {
            _handler.requestReward(player);
        }
    }

    public int getStatus(L2PcInstance player) {
        return _handler != null ? _handler.getStatus(player) : DailyMissionStatus.NOT_AVAILABLE.getClientId();
    }

    public int getProgress(L2PcInstance player) {
        return _handler != null ? _handler.getProgress(player) : DailyMissionStatus.NOT_AVAILABLE.getClientId();
    }

    public boolean getRecentlyCompleted(L2PcInstance player) {
        return (_handler != null) && _handler.getRecentlyCompleted(player);
    }

    public void reset() {
        if (_handler != null) {
            _handler.reset();
        }
    }
}
