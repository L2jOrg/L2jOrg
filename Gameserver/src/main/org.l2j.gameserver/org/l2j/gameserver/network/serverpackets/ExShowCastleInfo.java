package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.enums.TaxType;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * @author KenM
 */
public class ExShowCastleInfo extends IClientOutgoingPacket {
    public static final ExShowCastleInfo STATIC_PACKET = new ExShowCastleInfo();

    private ExShowCastleInfo() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SHOW_CASTLE_INFO.writeId(packet);

        final Collection<Castle> castles = CastleManager.getInstance().getCastles();
        packet.putInt(castles.size());
        for (Castle castle : castles) {
            packet.putInt(castle.getResidenceId());
            if (castle.getOwnerId() > 0) {
                if (ClanTable.getInstance().getClan(castle.getOwnerId()) != null) {
                    writeString(ClanTable.getInstance().getClan(castle.getOwnerId()).getName(), packet);
                } else {
                    LOGGER.warn("Castle owner with no name! Castle: " + castle.getName() + " has an OwnerId = " + castle.getOwnerId() + " who does not have a  name!");
                    writeString("", packet);
                }
            } else {
                writeString("", packet);
            }
            packet.putInt(castle.getTaxPercent(TaxType.BUY));
            packet.putInt((int) (castle.getSiege().getSiegeDate().getTimeInMillis() / 1000));

            packet.put((byte)( castle.getSiege().isInProgress() ? 0x01 : 0x00)); // Grand Crusade
            packet.put((byte) castle.getSide().ordinal()); // Grand Crusade
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 9 + CastleManager.getInstance().getCastles().size() * 100;
    }
}
