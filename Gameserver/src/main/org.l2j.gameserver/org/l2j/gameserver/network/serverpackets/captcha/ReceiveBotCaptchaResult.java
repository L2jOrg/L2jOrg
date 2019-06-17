package org.l2j.gameserver.network.serverpackets.captcha;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

@StaticPacket
public class ReceiveBotCaptchaResult extends ServerPacket {

    public static final ReceiveBotCaptchaResult SUCCESS = new ReceiveBotCaptchaResult(0x01);
    public static final ReceiveBotCaptchaResult FAILED = new ReceiveBotCaptchaResult(0x00);

    private final int answer;

    private ReceiveBotCaptchaResult(int answer) {
        this.answer = answer;
    }

    @Override
    protected void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.RECEIVE_BOT_CAPTCHA_ANSWER_RESULT);
        writeInt(answer);
    }

}
