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
package org.l2j.gameserver.engine.mission;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.holders.ItemHolder;

import java.time.DayOfWeek;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.function.Function;

import static java.util.Objects.nonNull;

/**
 * @author Sdw
 */
public class MissionDataHolder {

    private final int id;
    private final List<ItemHolder> rewardsItems;
    private final List<ClassId> classRestriction;
    private final int requiredCompletions;
    private final StatsSet params;
    private final MissionCycle cycle;

    private final boolean isDisplayedWhenNotAvailable;
    private final AbstractMissionHandler handler;
    private final int requiresMission;

    public MissionDataHolder(StatsSet set) {
        final Function<MissionDataHolder, AbstractMissionHandler> handler = MissionEngine.getInstance().getHandler(set.getString("handler"));

        id = set.getInt("id");
        requiredCompletions = set.getInt("required-completion", 1);
        rewardsItems = set.getList("rewards", ItemHolder.class);
        classRestriction = set.getList("classRestriction", ClassId.class);
        params = set.getObject("params", StatsSet.class);
        cycle = set.getEnum("cycle", MissionCycle.class);
        isDisplayedWhenNotAvailable = set.getBoolean("display-not-available", true);
        requiresMission = set.getInt("requires-mission", 0);
        this.handler = handler != null ? handler.apply(this) : null;
    }

    public int getId() {
        return id;
    }

    public List<ItemHolder> getRewards() {
        return rewardsItems;
    }

    public int getRequiredCompletions() {
        return requiredCompletions;
    }

    public StatsSet getParams() {
        return params;
    }

    public boolean isOneTime() {
        return cycle == MissionCycle.SINGLE;
    }

    public boolean isDisplayedWhenNotAvailable() {
        return isDisplayedWhenNotAvailable;
    }

    public boolean isDisplayable(Player player) {
        // Check for specific class restrictions
        if (!classRestriction.isEmpty() && !classRestriction.contains(player.getClassId())) {
            return false;
        }

        if(requiresMission != 0 && !MissionData.getInstance().isCompleted(player, requiresMission)) {
            return false;
        }

        final int status = getStatus(player);
        if (!isDisplayedWhenNotAvailable() && (status == MissionStatus.NOT_AVAILABLE.getClientId())) {
            return false;
        }

        // Show only if its repeatable, recently completed or uncompleted that has met the checks above.
        return (!isOneTime() || getRecentlyCompleted(player) || (status != MissionStatus.COMPLETED.getClientId()));
    }

    public void requestReward(Player player) {
        if (nonNull(handler)) {
            handler.requestReward(player);
        }
    }

    public int getStatus(Player player) {
        return nonNull(handler) ? handler.getStatus(player) : MissionStatus.NOT_AVAILABLE.getClientId();
    }

    public int getProgress(Player player) {
        return handler != null ? handler.getProgress(player) : 0;
    }

    public boolean getRecentlyCompleted(Player player) {
        return (handler != null) && handler.isRecentlyCompleted(player);
    }

    public void reset(long lastReset) {
        if (handler != null && canReset(lastReset)) {
            handler.reset();
        }
    }

    private boolean canReset(long lastReset) {
        return switch(cycle) {
            case SINGLE -> false;
            case DAILY -> true;
            case WEEKLY -> resetWeekly(lastReset);
            case MONTHLY -> resetMonthly(lastReset);
        };
    }

    private boolean resetMonthly(long lastReset) {
        var today = LocalDate.now();
        return today.getDayOfMonth() == 1 || today.with(TemporalAdjusters.firstDayOfMonth()).isAfter(LocalDate.ofInstant(Instant.ofEpochMilli(lastReset), ZoneId.systemDefault()));
    }

    private boolean resetWeekly(long lastReset) {
        var today = LocalDate.now();
        return today.getDayOfWeek() == DayOfWeek.SATURDAY || today.with(TemporalAdjusters.previous(DayOfWeek.SATURDAY)).isAfter(LocalDate.ofInstant(Instant.ofEpochMilli(lastReset), ZoneId.systemDefault()));
    }

    public boolean isAvailable(Player player) {
        return nonNull(handler) && handler.isAvailable(player);
    }

    public boolean isCompleted(Player player) {
        return getStatus(player) == MissionStatus.COMPLETED.getClientId();
    }
}
