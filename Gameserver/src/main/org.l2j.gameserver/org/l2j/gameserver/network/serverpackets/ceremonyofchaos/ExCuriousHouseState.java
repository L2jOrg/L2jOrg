package org.l2j.gameserver.network.serverpackets.ceremonyofchaos;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

/**
 * @author Sdw
 */
@StaticPacket
public class ExCuriousHouseState extends ServerPacket {
    public static final ExCuriousHouseState IDLE_PACKET = new ExCuriousHouseState(0);
    public static final ExCuriousHouseState REGISTRATION_PACKET = new ExCuriousHouseState(1);
    public static final ExCuriousHouseState PREPARE_PACKET = new ExCuriousHouseState(2);
    public static final ExCuriousHouseState STARTING_PACKET = new ExCuriousHouseState(3);

    private final int _state;

    private ExCuriousHouseState(int state) {
        _state = state;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.EX_CURIOUS_HOUSE_STATE);
        writeInt(_state);
    }

}