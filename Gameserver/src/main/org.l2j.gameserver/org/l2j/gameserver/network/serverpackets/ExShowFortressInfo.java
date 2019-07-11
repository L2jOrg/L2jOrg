package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.instancemanager.FortDataManager;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.entity.Fort;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Collection;

/**
 * @author KenM
 */
@StaticPacket
public class ExShowFortressInfo extends ServerPacket {
    public static final ExShowFortressInfo STATIC_PACKET = new ExShowFortressInfo();

    private ExShowFortressInfo() {

    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_SHOW_FORTRESS_INFO);

        final Collection<Fort> forts = FortDataManager.getInstance().getForts();
        writeInt(forts.size());
        for (Fort fort : forts) {
            final L2Clan clan = fort.getOwnerClan();
            writeInt(fort.getResidenceId());
            writeString(clan != null ? clan.getName() : "");
            writeInt(fort.getSiege().isInProgress() ? 0x01 : 0x00);
            // Time of possession
            writeInt(fort.getOwnedTime());
        }
    }

}
