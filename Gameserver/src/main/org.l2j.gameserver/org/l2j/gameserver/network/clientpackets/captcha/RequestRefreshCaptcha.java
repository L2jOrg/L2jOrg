package org.l2j.gameserver.network.clientpackets.captcha;

import org.l2j.gameserver.engines.captcha.CaptchaEngine;
import org.l2j.gameserver.engines.captcha.CaptchaEngine.Captcha;
import org.l2j.gameserver.model.actor.request.impl.CaptchaRequest;
import org.l2j.gameserver.network.clientpackets.IClientIncomingPacket;
import org.l2j.gameserver.network.serverpackets.captcha.ReceiveBotCaptchaImage;

import java.nio.ByteBuffer;

import static java.util.Objects.nonNull;

public class RequestRefreshCaptcha extends IClientIncomingPacket {

    private long captchaId;

    @Override
    protected void readImpl() throws Exception {
        captchaId = readLong();
    }

    @Override
    protected void runImpl()  {
        var player = client.getActiveChar();
        var request = player.getRequest(CaptchaRequest.class);
        Captcha captcha = CaptchaEngine.getInstance().next((int) captchaId);
        if(nonNull(request)) {
            request.refresh(captcha);
        } else {
            request = new CaptchaRequest(player, captcha);
            player.addRequest(request);
        }
        player.sendPacket(new ReceiveBotCaptchaImage(captcha, request.getRemainingTime()));
    }
}
