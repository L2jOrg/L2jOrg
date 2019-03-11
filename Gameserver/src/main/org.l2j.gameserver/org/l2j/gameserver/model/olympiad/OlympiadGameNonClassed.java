package org.l2j.gameserver.model.olympiad;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.holders.ItemHolder;

import java.util.List;
import java.util.Set;

/**
 * @author DS
 */
public class OlympiadGameNonClassed extends OlympiadGameNormal {
    public OlympiadGameNonClassed(int id, Participant[] opponents) {
        super(id, opponents);
    }

    protected static OlympiadGameNonClassed createGame(int id, Set<Integer> list) {
        final Participant[] opponents = OlympiadGameNormal.createListOfParticipants(list);
        if (opponents == null) {
            return null;
        }

        return new OlympiadGameNonClassed(id, opponents);
    }

    @Override
    public final CompetitionType getType() {
        return CompetitionType.NON_CLASSED;
    }

    @Override
    protected final int getDivider() {
        return Config.ALT_OLY_DIVIDER_NON_CLASSED;
    }

    @Override
    protected final List<ItemHolder> getReward() {
        return Config.ALT_OLY_NONCLASSED_REWARD;
    }

    @Override
    protected final String getWeeklyMatchType() {
        return COMP_DONE_WEEK_NON_CLASSED;
    }
}
