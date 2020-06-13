/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.network.clientpackets.captcha;

import org.l2j.gameserver.engine.captcha.Captcha;
import org.l2j.gameserver.engine.captcha.CaptchaEngine;
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
