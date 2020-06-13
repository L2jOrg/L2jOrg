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
package org.l2j.gameserver.model.olympiad;

import org.l2j.gameserver.Config;

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

}
