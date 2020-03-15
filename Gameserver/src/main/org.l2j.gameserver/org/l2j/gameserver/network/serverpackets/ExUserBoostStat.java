package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author JoeAlisson
 */
public class ExUserBoostStat extends ServerPacket{

    private final BoostStatType type;
    private final short percent;

    public ExUserBoostStat(BoostStatType type, short percent) {
        this.type =type;
        this.percent =percent;
    }

    @Override
    protected void writeImpl(GameClient client)  {
        writeId(ServerPacketId.EX_USER_BOOST_STAT);
        writeByte(type.ordinal() + 1); // type (Server bonus), 2 - (stats bonus) or 3 (Vitality) ?
        writeByte(1); // count
        writeShort(percent);
    }

    public enum BoostStatType {
        SERVER,
        STAT,
        OTHER,
    }
}
