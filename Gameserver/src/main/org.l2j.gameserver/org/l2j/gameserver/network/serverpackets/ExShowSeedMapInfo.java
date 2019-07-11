package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.instancemanager.GraciaSeedsManager;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

@StaticPacket
public class ExShowSeedMapInfo extends ServerPacket {
    public static final ExShowSeedMapInfo STATIC_PACKET = new ExShowSeedMapInfo();

    private ExShowSeedMapInfo() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.EX_SHOW_SEED_MAP_INFO);

        writeInt(2); // seed count

        // Seed of Destruction
        writeInt(1); // id 1? Grand Crusade
        writeInt(2770 + GraciaSeedsManager.getInstance().getSoDState()); // sys msg id

        // Seed of Infinity
        writeInt(2); // id 2? Grand Crusade
        // Manager not implemented yet
        writeInt(2766); // sys msg id
    }

}
