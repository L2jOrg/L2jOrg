package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.instancemanager.FortManager;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.entity.Fort;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Collection;

/**
 * @author KenM
 */
@StaticPacket
public class ExShowFortressInfo extends IClientOutgoingPacket {
    public static final ExShowFortressInfo STATIC_PACKET = new ExShowFortressInfo();

    private ExShowFortressInfo() {

    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_SHOW_FORTRESS_INFO.writeId(packet);

        final Collection<Fort> forts = FortManager.getInstance().getForts();
        packet.putInt(forts.size());
        for (Fort fort : forts) {
            final L2Clan clan = fort.getOwnerClan();
            packet.putInt(fort.getResidenceId());
            writeString(clan != null ? clan.getName() : "", packet);
            packet.putInt(fort.getSiege().isInProgress() ? 0x01 : 0x00);
            // Time of possession
            packet.putInt(fort.getOwnedTime());
        }
    }

    @Override
    protected int size(L2GameClient client) {
        return 9 + FortManager.getInstance().getForts().size() * 50;
    }
}
