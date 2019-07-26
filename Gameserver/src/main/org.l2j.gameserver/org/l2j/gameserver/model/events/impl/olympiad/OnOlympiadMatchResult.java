package org.l2j.gameserver.model.events.impl.olympiad;

import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;
import org.l2j.gameserver.model.olympiad.CompetitionType;
import org.l2j.gameserver.model.olympiad.Participant;

/**
 * @author UnAfraid
 */
public class OnOlympiadMatchResult implements IBaseEvent {
    private final Participant _winner;
    private final Participant _loser;
    private final CompetitionType _type;

    public OnOlympiadMatchResult(Participant winner, Participant looser, CompetitionType type) {
        _winner = winner;
        _loser = looser;
        _type = type;
    }

    public Participant getWinner() {
        return _winner;
    }

    public Participant getLoser() {
        return _loser;
    }

    public CompetitionType getCompetitionType() {
        return _type;
    }

    @Override
    public EventType getType() {
        return EventType.ON_OLYMPIAD_MATCH_RESULT;
    }
}
