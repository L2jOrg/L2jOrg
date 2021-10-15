package org.l2j.gameserver.network;

public class SimpleRateLimiter {
    private static final int REFILL_TIME = 1000;
    private static final int REFILL_AMOUNT = 100;
    private static final int MAX_AMOUNT = 300;

    private long lastUpdate = System.currentTimeMillis();
    private int amount = MAX_AMOUNT;

    public SimpleRateLimiter(GameClient client) {

    }

    public boolean consume() {
        refill();
        if(amount > 1) {
            amount--;
            return true;
        }
        return false;
    }

    private void refill() {
        var count = Math.min(Math.max(0, (System.currentTimeMillis() - lastUpdate) / REFILL_TIME * REFILL_AMOUNT), MAX_AMOUNT);
        amount += count;
        lastUpdate += count * REFILL_TIME;
    }
}