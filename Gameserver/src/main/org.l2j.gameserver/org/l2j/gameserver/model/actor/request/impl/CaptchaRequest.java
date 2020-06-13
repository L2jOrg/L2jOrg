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
package org.l2j.gameserver.model.actor.request.impl;

import org.l2j.gameserver.datatables.ReportTable;
import org.l2j.gameserver.engine.captcha.Captcha;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.AbstractRequest;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static java.lang.System.currentTimeMillis;

public class CaptchaRequest extends AbstractRequest {

    private static final byte MAX_ATTEMPTS = 3;
    private static final int DURATION = 20;
    private Captcha captcha;
    private byte count = 0;
    private final Instant timeout;

    public CaptchaRequest(Player activeChar, Captcha captcha) {
        super(activeChar);
        this.captcha = captcha;
        var currentTime = currentTimeMillis();
        setTimestamp(currentTime);
        scheduleTimeout(Duration.ofMinutes(DURATION).toMillis());
        timeout = Instant.ofEpochMilli(currentTime).plus(DURATION, ChronoUnit.MINUTES);
    }

    @Override
    public boolean isUsing(int objectId) {
        return false;
    }

    public int getRemainingTime() {
        return (int) (timeout.minusMillis(currentTimeMillis()).getEpochSecond());
    }

    public void refresh(Captcha captcha) {
        this.captcha = captcha;
    }

    public void newRequest(Captcha captcha) {
        count++;
        this.captcha = captcha;
    }

    public boolean isLimitReached() {
        return count >= MAX_ATTEMPTS -1;
    }

    public Captcha getCaptcha() {
        return captcha;
    }

    @Override
    public void onTimeout() {
        ReportTable.getInstance().punishBotDueUnsolvedCaptcha(getPlayer());
    }

    public int maxAttemps() {
        return MAX_ATTEMPTS;
    }

    public int remainingAttemps() {
        return Math.max(MAX_ATTEMPTS - count, 0);
    }
}
