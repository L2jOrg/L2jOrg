package org.l2j.gameserver.network.serverpackets.captcha;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerExPacketId;
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
    protected void writeImpl(GameClient client) {
        writeId(ServerExPacketId.EX_CAPTCHA_ANSWER_RESULT);
        writeInt(answer);
    }

}
