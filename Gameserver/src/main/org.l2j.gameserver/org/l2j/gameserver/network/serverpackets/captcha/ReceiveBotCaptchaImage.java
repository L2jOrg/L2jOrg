package org.l2j.gameserver.network.serverpackets.captcha;

import org.l2j.gameserver.engines.captcha.CaptchaEngine.Captcha;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

public class ReceiveBotCaptchaImage extends IClientOutgoingPacket {

    private final Captcha captcha;
    private final int time;

    public ReceiveBotCaptchaImage(Captcha captcha, int time) {
        this.captcha = captcha;
        this.time = time;
    }

    @Override
    protected void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.RECEIVE_BOT_CAPTCHA_IMAGE.writeId(packet);
        packet.putLong(captcha.getId());
        packet.put((byte) 0x02); // unk
        packet.putInt(time);
        packet.put(captcha.getData());
    }

    @Override
    protected int size(L2GameClient client) {
        return 18 + captcha.getData().length;
    }
}
