package org.l2j.gameserver.engine.mission;

import org.l2j.gameserver.handler.AbstractMissionHandler;
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

    public MissionDataHolder(StatsSet set) {
        final Function<MissionDataHolder, AbstractMissionHandler> handler = MissionEngine.getInstance().getHandler(set.getString("handler"));

        id = set.getInt("id");
        requiredCompletions = set.getInt("requiredCompletion", 1);
        rewardsItems = set.getList("rewards", ItemHolder.class);
        classRestriction = set.getList("classRestriction", ClassId.class);
        params = set.getObject("params", StatsSet.class);
        cycle = set.getEnum("cycle", MissionCycle.class);
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
}
