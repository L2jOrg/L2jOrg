package org.l2j.gameserver.network.serverpackets.captcha;

import io.github.joealisson.mmocore.StaticPacket;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

@StaticPacket
public class ReceiveBotCaptchaResult extends IClientOutgoingPacket {

    public static final ReceiveBotCaptchaResult SUCCESS = new ReceiveBotCaptchaResult(0x01);
    public static final ReceiveBotCaptchaResult FAILED = new ReceiveBotCaptchaResult(0x00);

    private final int answer;

    private ReceiveBotCaptchaResult(int answer) {
        this.answer = answer;
    }

    @Override
    protected void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.RECEIVE_BOT_CAPTCHA_ANSWER_RESULT.writeId(packet);
        packet.putInt(answer);
    }

    @Override
    protected int size(L2GameClient client) {
        return 9;
    }
}
