package org.l2j.gameserver.network.serverpackets.pledge;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.ClanWar;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Collection;

public class ExPledgeEnemyInfoList extends ServerPacket {
    private final Player _player;
    public ExPledgeEnemyInfoList(Player player)
    {
        _player = player;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerExPacketId.EX_PLEDGE_ENEMY_INFO_LIST, buffer);
        Clan clan = _player.getClan();
        if (clan == null)
        {
            return;
        }

        Collection<ClanWar> clanList = clan.getWarList().values();
        buffer.writeInt(clanList.size());
        for (ClanWar clanWar : clanList)
        {
            final Clan enemy = clanWar.getOpposingClan(clan);
            if (enemy == null)
            {
                continue;
            }
            //buffer.writeInt(Config.SERVER_ID);
            buffer.writeInt(enemy.getId());
            buffer.writeString(enemy.getName());
            buffer.writeString(enemy.getLeaderName());
        }
    }
}
