package org.l2j.gameserver.model.dailymission;

import org.l2j.gameserver.handler.AbstractDailyMissionHandler;
import org.l2j.gameserver.handler.DailyMissionHandler;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.holders.ItemHolder;

import java.util.Calendar;
import java.util.List;
import java.util.function.Function;

/**
 * @author Sdw
 */
public class DailyMissionDataHolder {

    private final int id;
    private final List<ItemHolder> rewardsItems;
    private final List<ClassId> classRestriction;
    private final int requiredCompletions;
    private final StatsSet params;
    private final DailyMissionCycle cycle;

    private final boolean isDisplayedWhenNotAvailable;
    private final AbstractDailyMissionHandler handler;

    public DailyMissionDataHolder(StatsSet set) {
        final Function<DailyMissionDataHolder, AbstractDailyMissionHandler> handler = DailyMissionHandler.getInstance().getHandler(set.getString("handler"));

        id = set.getInt("id");
        requiredCompletions = set.getInt("requiredCompletion", 1);
        rewardsItems = set.getList("rewards", ItemHolder.class);
        classRestriction = set.getList("classRestriction", ClassId.class);
        params = set.getObject("params", StatsSet.class);
        cycle = set.getEnum("cycle", DailyMissionCycle.class);
        isDisplayedWhenNotAvailable = set.getBoolean("isDisplayedWhenNotAvailable", true);
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
        return cycle == DailyMissionCycle.SINGLE;
    }

    public boolean isDisplayedWhenNotAvailable() {
        return isDisplayedWhenNotAvailable;
    }

    public boolean isDisplayable(L2PcInstance player) {
        // Check for specific class restrictions
        if (!classRestriction.isEmpty() && !classRestriction.contains(player.getClassId())) {
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
        if ((handler != null) && isDisplayable(player)) {
            handler.requestReward(player);
        }
    }

    public int getStatus(L2PcInstance player) {
        return handler != null ? handler.getStatus(player) : DailyMissionStatus.NOT_AVAILABLE.getClientId();
    }

    public int getProgress(L2PcInstance player) {
        return handler != null ? handler.getProgress(player) : DailyMissionStatus.NOT_AVAILABLE.getClientId();
    }

    public boolean getRecentlyCompleted(L2PcInstance player) {
        return (handler != null) && handler.getRecentlyCompleted(player);
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
        // TODO check last reset
        return Calendar.getInstance().get(Calendar.DAY_OF_MONTH) == 1;
    }

    private boolean resetWeekly(long lastReset) {
        // TODO check last reset
        return Calendar.getInstance().get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY;
    }
}
