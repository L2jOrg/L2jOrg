package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.engine.olympiad.PlayerMatchResult;
import org.l2j.gameserver.model.actor.instance.Player;

import java.time.Duration;
import java.time.LocalDateTime;

/**
 * @author JoeAlisson
 */
@Table("olympiad_matches")
public class OlympiadMatchResultData {

    @Column("player_id")
    private int playerId;
    private int server;

    @Column("class_id")
    private int classId;
    private int opponent;
    private LocalDateTime date;
    private Duration duration;
    private PlayerMatchResult result;
    private int win;
    private int loss;
    private int tie;

    @NonUpdatable
    @Column("opponent_name")
    private String opponentName;

    @NonUpdatable
    @Column("opponent_class_id")
    private int opponentClassId;

    public static OlympiadMatchResultData of(Player player, int server, Player opponent, PlayerMatchResult result, OlympiadParticipantData participantData, Duration battleDuration) {
        final var data = new OlympiadMatchResultData();
        data.playerId = player.getObjectId();
        data.server = server;
        data.classId = player.getClassId().getId();
        data.opponent = opponent.getObjectId();
        data.date = LocalDateTime.now();
        data.duration = battleDuration;
        data.result = result;
        data.win = participantData.getBattlesWon();
        data.loss = participantData.getBattlesLost();
        data.tie = participantData.getBattles() - data.win + data.loss;
        return data;
    }

    public int getPlayerId() {
        return playerId;
    }

    public int getServer() {
        return server;
    }

    public int getClassId() {
        return classId;
    }

    public int getOpponent() {
        return opponent;
    }

    public String getOpponentName() {
        return opponentName;
    }

    public int getOpponentClassId() {
        return opponentClassId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public Duration getDuration() {
        return duration;
    }

    public PlayerMatchResult getResult() {
        return result;
    }

    public int getWin() {
        return win;
    }

    public int getLoss() {
        return loss;
    }

    public int getTie() {
        return tie;
    }


}
