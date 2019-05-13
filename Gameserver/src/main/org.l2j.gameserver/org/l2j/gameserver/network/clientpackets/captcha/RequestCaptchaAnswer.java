package org.l2j.gameserver.network.clientpackets.captcha;

import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;

import java.nio.ByteBuffer;

public class RequestCaptchaAnswer extends IClientIncomingPacket {

    @Override
    protected void readImpl(ByteBuffer packet) throws Exception {
        var unk = packet.getInt();
        var unk2 = packet.getInt();
        var answer = packet.getInt();
    }

    @Override
    protected void runImpl() throws Exception {

    }
}
