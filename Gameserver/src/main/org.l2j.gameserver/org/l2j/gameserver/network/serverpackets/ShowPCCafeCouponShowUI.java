package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author -Wooden-
 */
@StaticPacket
public class ShowPCCafeCouponShowUI extends IClientOutgoingPacket {
    public static final ShowPCCafeCouponShowUI STATIC_PACKET = new ShowPCCafeCouponShowUI();

    private ShowPCCafeCouponShowUI() {
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.SHOW_PCCAFE_COUPON_SHOW_UI.writeId(packet);

    }
}
