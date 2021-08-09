/*
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.model.events.impl.olympiad;

import org.l2j.gameserver.engine.olympiad.OlympiadResultInfo;
import org.l2j.gameserver.engine.olympiad.OlympiadRuleType;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

/**
 * @author UnAfraid
 */
public class OnOlympiadMatchResult implements IBaseEvent {
    private final OlympiadResultInfo _winner;
    private final OlympiadResultInfo _loser;
    private final OlympiadRuleType _type;

    public OnOlympiadMatchResult(OlympiadResultInfo winner, OlympiadResultInfo loser, OlympiadRuleType type) {
        _winner = winner;
        _loser = loser;
        _type = type;
    }

    public OlympiadResultInfo getWinner() {
        return _winner;
    }

    public OlympiadResultInfo getLoser() {
        return _loser;
    }

    public OlympiadRuleType getCompetitionType() {
        return _type;
    }

    @Override
    public EventType getType() {
        return EventType.ON_OLYMPIAD_MATCH_RESULT;
    }
}
