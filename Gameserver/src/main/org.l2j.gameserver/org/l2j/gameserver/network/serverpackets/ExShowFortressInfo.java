package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.instancemanager.FortDataManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.entity.Fort;
import org.l2j.gameserver.network.GameClient;
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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_SHOW_FORTRESS_INFO);

        final Collection<Fort> forts = FortDataManager.getInstance().getForts();
        writeInt(forts.size());
        for (Fort fort : forts) {
            final Clan clan = fort.getOwnerClan();
            writeInt(fort.getId());
            writeString(clan != null ? clan.getName() : "");
            writeInt(fort.getSiege().isInProgress() ? 0x01 : 0x00);
            // Time of possession
            writeInt(fort.getOwnedTime());
        }
    }

}
