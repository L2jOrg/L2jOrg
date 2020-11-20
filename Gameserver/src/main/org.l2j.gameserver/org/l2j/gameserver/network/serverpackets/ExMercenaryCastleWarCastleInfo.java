package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.enums.TaxType;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;


public class ExMercenaryCastleWarCastleInfo extends ServerPacket {
    private final int _castleId;

    public ExMercenaryCastleWarCastleInfo(int castleId)
    {
        _castleId = castleId;
    }

    @Override
    protected void writeImpl(GameClient client, WritableBuffer buffer) throws Exception {
        writeId(ServerExPacketId.EX_MERCENARY_CASTLEWAR_CASTLE_INFO, buffer );
        Castle castle = CastleManager.getInstance().getCastleById(_castleId);
        if (castle == null)
        {
            buffer.writeInt(_castleId);
            buffer.writeInt(0x00);
            buffer.writeInt(0x00);
            buffer.writeString("");
            buffer.writeString("");
            buffer.writeInt(0x00);
            buffer.writeLong(0x00);
            buffer.writeInt(0x00);
            buffer.writeInt(0x00);
            buffer.writeInt(0x00);
        }
        if (castle != null){
            buffer.writeInt(_castleId);
            buffer.writeInt(castle.getOwner() != null ? castle.getOwner().getCrestId() : 0); // Is that crest?
            buffer.writeInt(0x00); // ?

            buffer.writeString(castle.getOwner() != null ? castle.getOwner().getName() : "-");
            buffer.writeString(castle.getOwner() != null ? castle.getOwner().getLeaderName() : "-");

            buffer.writeInt(castle.getTaxPercent(TaxType.BUY));
            buffer.writeLong(castle.getTreasury());
            buffer.writeInt(castle.getOwner() != null ? castle.getOwner().getCrestId() : 0); // Is that crest?
            buffer.writeInt(0x00); // ?
            buffer.writeInt(0x00); // ?
           // buffer.writeInt(castle.getSiegeDate() != null ? (int) (CastleManager.getInstance().getCastleById(_castleId).getSiegeDate()).toMillis() / 1000) : 0);
        }
    }
}
