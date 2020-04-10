package org.l2j.gameserver.network.serverpackets.captcha;

import org.l2j.gameserver.engine.captcha.Captcha;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

public class ReceiveBotCaptchaImage extends ServerPacket {

    private final Captcha captcha;
    private final int time;

    public ReceiveBotCaptchaImage(Captcha captcha, int time) {
        this.captcha = captcha;
        this.time = time;
    }

    @Override
    protected void writeImpl(GameClient client) {
        writeId(ServerPacketId.RECEIVE_BOT_CAPTCHA_IMAGE);
        writeLong(captcha.getId());
        writeByte((byte) 0x02); // unk
        writeInt(time);
        writeBytes(captcha.getData());
    }

}
