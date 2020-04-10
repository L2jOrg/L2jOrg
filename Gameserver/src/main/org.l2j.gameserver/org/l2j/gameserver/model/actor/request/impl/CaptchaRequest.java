package org.l2j.gameserver.model.actor.request.impl;

import org.l2j.gameserver.datatables.ReportTable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.request.AbstractRequest;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static java.lang.System.currentTimeMillis;

import org.l2j.gameserver.engine.captcha.Captcha;

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
        ReportTable.getInstance().punishBotDueUnsolvedCaptcha(getActiveChar());
    }

    public int maxAttemps() {
        return MAX_ATTEMPTS;
    }

    public int remainingAttemps() {
        return Math.max(MAX_ATTEMPTS - count, 0);
    }
}
