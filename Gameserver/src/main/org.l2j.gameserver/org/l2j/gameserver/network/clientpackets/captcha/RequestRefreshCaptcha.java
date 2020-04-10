package org.l2j.gameserver.network.clientpackets.captcha;

import org.l2j.gameserver.engine.captcha.CaptchaEngine;
import org.l2j.gameserver.engine.captcha.Captcha;
import org.l2j.gameserver.model.actor.request.impl.CaptchaRequest;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.captcha.ReceiveBotCaptchaImage;

import static java.util.Objects.nonNull;

public class RequestRefreshCaptcha extends ClientPacket {

    private long captchaId;

    @Override
    protected void readImpl() throws Exception {
        captchaId = readLong();
    }

    @Override
    protected void runImpl()  {
        var player = client.getPlayer();
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
