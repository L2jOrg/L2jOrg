package org.l2j.gameserver.engine.autoplay;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author JoeAlisson
 */
public class AutoPlaySettings {

    private short size;
    private boolean active;
    private boolean pickUp;
    private short nextTargetMode;
    private boolean isNearTarget;
    private int usableHpPotionPercent;
    private boolean respectFulHunt;
    private final AtomicBoolean autoPlaying = new AtomicBoolean(false);
    private int usableHpPetPotionPercent;

    public AutoPlaySettings(short size, boolean active, boolean pickUp, short nextTargetMode, boolean isNearTarget, int hpPotionPercent, int usableHpPotionPercent, boolean respectFulHunt) {
        this.size = size;
        this.active = active;
        this.pickUp = pickUp;
        this.nextTargetMode = nextTargetMode;
        this.isNearTarget = isNearTarget;
        this.usableHpPotionPercent =  usableHpPotionPercent;
        this.respectFulHunt = respectFulHunt;
    }

    public short getSize() {
        return size;
    }

    public void setSize(short options) {
        this.size = options;
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

    public void setUsableHpPetPotionPercent(int usableHpPetPotionPercent) {
        this.usableHpPetPotionPercent = usableHpPetPotionPercent;
    }

    public int getUsableHpPetPotionPercent() {
        return usableHpPetPotionPercent;
    }
}
