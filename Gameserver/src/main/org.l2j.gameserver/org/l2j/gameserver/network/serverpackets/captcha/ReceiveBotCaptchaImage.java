package org.l2j.gameserver.network.serverpackets.captcha;

import org.l2j.gameserver.engines.captcha.CaptchaEngine;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

public class ReceiveBotCaptchaImage extends IClientOutgoingPacket {


    @Override
    protected void writeImpl(L2GameClient client, ByteBuffer packet) throws Exception {

        OutgoingPackets.RECEIVE_BOT_CAPTCHA_IMAGE.writeId(packet);
        packet.put((byte) 0x02); // unk
        packet.putLong(0x02);   // unk
        packet.putInt(1200); // time
        var captcha = CaptchaEngine.getInstance().next();
        packet.put(captcha.getData());
    }
}
