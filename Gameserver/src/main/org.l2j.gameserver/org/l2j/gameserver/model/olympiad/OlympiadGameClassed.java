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
package org.l2j.gameserver.model.olympiad;

import org.l2j.gameserver.Config;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.holders.ItemHolder;

import java.util.List;
import java.util.Set;

/**
 * @author DS
 */
public class OlympiadGameClassed extends OlympiadGameNormal {
    private OlympiadGameClassed(int id, Participant[] opponents) {
        super(id, opponents);
    }

    protected static OlympiadGameClassed createGame(int id, List<Set<Integer>> classList) {
        if ((classList == null) || classList.isEmpty()) {
            return null;
        }

        Set<Integer> list;
        Participant[] opponents;
        while (!classList.isEmpty()) {
            list = classList.get(Rnd.get(classList.size()));
            if ((list == null) || (list.size() < 2)) {
                classList.remove(list);
                continue;
            }

            opponents = OlympiadGameNormal.createListOfParticipants(list);
            if (opponents == null) {
                classList.remove(list);
                continue;
            }

            return new OlympiadGameClassed(id, opponents);
        }
        return null;
    }

    @Override
    public final CompetitionType getType() {
        return CompetitionType.CLASSED;
    }

    @Override
    protected final int getDivider() {
        return Config.ALT_OLY_DIVIDER_CLASSED;
    }

    @Override
    protected final List<ItemHolder> getReward() {
        return Config.ALT_OLY_CLASSED_REWARD;
    }

    @Override
    protected final String getWeeklyMatchType() {
        return COMP_DONE_WEEK_CLASSED;
    }
}