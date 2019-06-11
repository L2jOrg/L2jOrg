package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
public class ExSetCompassZoneCode extends IClientOutgoingPacket {
    // TODO: Enum
    public static final int ALTEREDZONE = 0x08;
    public static final int SIEGEWARZONE1 = 0x0A;
    public static final int SIEGEWARZONE2 = 0x0B;
    public static final int PEACEZONE = 0x0C;
    public static final int SEVENSIGNSZONE = 0x0D;
    public static final int PVPZONE = 0x0E;
    public static final int GENERALZONE = 0x0F;

    private final int _zoneType;

    public ExSetCompassZoneCode(int val) {
        _zoneType = val;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_SET_COMPASS_ZONE_CODE);

        writeInt(_zoneType);
    }

}
