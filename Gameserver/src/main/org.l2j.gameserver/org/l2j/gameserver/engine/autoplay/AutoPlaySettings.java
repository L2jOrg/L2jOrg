package org.l2j.gameserver.engine.autoplay;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author JoeAlisson
 */
public class AutoPlaySettings {

    private short options;
    private boolean active;
    private boolean pickUp;
    private short nextTargetMode;
    private boolean isNearTarget;
    private int usableHpPotionPercent;
    private boolean respectFulHunt;
    private AtomicBoolean autoPlaying = new AtomicBoolean(false);

    public AutoPlaySettings(short options, boolean active, boolean pickUp, short nextTargetMode, boolean isNearTarget, int usableHpPotionPercent, boolean respectFulHunt) {
        this.options = options;
        this.active = active;
        this.pickUp = pickUp;
        this.nextTargetMode = nextTargetMode;
        this.isNearTarget = isNearTarget;
        this.usableHpPotionPercent =  usableHpPotionPercent;
        this.respectFulHunt = respectFulHunt;
    }

    public short getOptions() {
        return options;
    }

    public void setOptions(short options) {
        this.options = options;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public boolean isAutoPickUpOn() {
        return pickUp;
    }

    public void setAutoPickUpOn(boolean pickUp) {
        this.pickUp = pickUp;
    }

    public short getNextTargetMode() {
        return nextTargetMode;
    }

    public void setNextTargetMode(short nextTargetMode) {
        this.nextTargetMode = nextTargetMode;
    }

    public boolean isNearTarget() {
        return isNearTarget;
    }

    public void setNearTarget(boolean isNearTarget) {
        this.isNearTarget = isNearTarget;
    }

    public int getUsableHpPotionPercent() {
        return usableHpPotionPercent;
    }

    public void setUsableHpPotionPercent(int usableHpPotionPercent) {
        this.usableHpPotionPercent = usableHpPotionPercent;
    }

    public boolean isRespectfulMode() {
        return respectFulHunt;
    }

    public void setRespectfulHunt(boolean respectfulHunt) {
        this.respectFulHunt = respectfulHunt;
    }

    public boolean isAutoPlaying() {
        return autoPlaying.get();
    }

    public void setAutoPlaying(boolean autoPlaying) {
        this.autoPlaying.set(autoPlaying);
    }
}
