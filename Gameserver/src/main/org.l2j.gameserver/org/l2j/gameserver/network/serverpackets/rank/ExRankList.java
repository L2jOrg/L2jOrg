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

/**
 * @author JoeAlisson
 */
public class ExRankList extends ServerPacket {

    private final int race;
    private final byte group;
    private final byte scope;
    private final int classe;
    private final List<RankData> rankers;

    public ExRankList(Player player, byte group, byte scope, int race, int classe) {
        this.group = group;
        this.scope = scope;
        this.race = race;
        this.classe = classe;

        rankers = switch (group) {
            case 0 -> listServerRankers(player, scope);
            case 1 -> listRaceRankers(player, scope, race);
            case 2 -> listClanRankers(player);
            case 3 -> listFriendsRankers(player);
            default -> Collections.emptyList();
        };
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer)  {
        writeId(ServerExPacketId.EX_RANKING_CHAR_RANKERS, buffer );
        buffer.writeByte(group);
        buffer.writeByte(scope);
        buffer.writeInt(race);
        buffer.writeInt(classe);


        buffer.writeInt(rankers.size());

        for (var ranker : rankers) {
            buffer.writeSizedString(ranker.getPlayerName());
            buffer.writeSizedString(ranker.getClanName());
            buffer.writeInt(ranker.getLevel());
            buffer.writeInt(ranker.getClassId());
            buffer.writeInt(ranker.getRace());
            buffer.writeInt(ranker.getRank());
            buffer.writeInt(ranker.getRankSnapshot());
            buffer.writeInt(ranker.getRankRaceSnapshot());
            buffer.writeInt(0); // TODO: ClassRank Snapshot
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
