package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

/**
 * @author -Wooden-
 */
@StaticPacket
public class ShowPCCafeCouponShowUI extends ServerPacket {
    public static final ShowPCCafeCouponShowUI STATIC_PACKET = new ShowPCCafeCouponShowUI();

    private ShowPCCafeCouponShowUI() {
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.SHOW_PCCAFE_COUPON_SHOW_UI);

    }

}
