package org.l2j.gameserver.network.serverpackets.rank;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.data.database.data.RankData;
import org.l2j.gameserver.engine.rank.RankEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Collections;
import java.util.List;

import static java.util.Objects.nonNull;

public class ExPvPRankingList extends ServerPacket {
    private final boolean currentSeason;
    private final int race;
    private final byte group;
    private final byte scope;

    private final List<RankData> rankers;

    public ExPvPRankingList(Player player, boolean currentSeason,  byte group, byte scope,int race) {
        this.currentSeason = currentSeason;
        this.group = group;
        this.scope = scope;
        this.race = race;

        rankers = switch (group) {
            case 0 -> listServerRankers(player, scope);
            case 1 -> listRaceRankers(player, scope, race);
            case 2 -> listClanRankers(player);
            case 3 -> listFriendsRankers(player);
            default -> Collections.emptyList();
        };
    }
    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_PVP_RANKING_LIST, buffer );
        buffer.writeByte(currentSeason);
        buffer.writeByte(group);
        buffer.writeByte(scope);
        buffer.writeInt(race);

        buffer.writeInt(rankers.size());

        for (var ranker : rankers) {
            buffer.writeSizedString(ranker.getPlayerName());
            buffer.writeSizedString(ranker.getClanName());
            buffer.writeInt(ranker.getLevel());
            buffer.writeInt(ranker.getRace());
            buffer.writeInt(ranker.getClassId());
            buffer.writeInt(5); //TODO pvpcount
            buffer.writeInt(ranker.getRank());
            buffer.writeInt(2); //TODO prev rank
            buffer.writeInt(5);// TODO: killcount
            buffer.writeInt(2);// TODO: diecount

        }
    }
    private List<RankData> listRaceRankers(Player player, byte scope, int race) {
        if(scope == 0) {
            return RankEngine.getInstance().getTopRaceRankers(race);
        }
        return RankEngine.getInstance().getRaceRankersByPlayer(player);
    }

    private List<RankData> listServerRankers(Player player, byte scope) {
        if(scope == 0) {
            return RankEngine.getInstance().getTopRankers();
        }
        return RankEngine.getInstance().getRankersByPlayer(player);
    }

    private List<RankData> listFriendsRankers(Player player) {
        return !player.getFriendList().isEmpty() ?  RankEngine.getInstance().getFriendRankers(player) : Collections.emptyList();
    }

    private List<RankData> listClanRankers(Player player) {
        return nonNull(player.getClan()) ?  RankEngine.getInstance().getClanRankers(player.getClanId()) : Collections.emptyList();
    }
}
