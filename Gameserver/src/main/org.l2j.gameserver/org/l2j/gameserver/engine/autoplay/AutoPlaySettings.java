package org.l2j.gameserver.engine.autoplay;

/**
 * @author JoeAlisson
 */
public class AutoPlaySettings {

    private final short options;
    private final boolean active;
    private final boolean pickUp;
    private final short nextTargetMode;
    private final boolean isNearTarget;
    private final int usableHpPotionPercent;
    private final boolean mannerMode;

    public AutoPlaySettings(short options, boolean active, boolean pickUp, short nextTargetMode, boolean isNearTarget, int usableHpPotionPercent, boolean mannerMode) {
        this.options = options;
        this.active = active;
        this.pickUp = pickUp;
        this.nextTargetMode = nextTargetMode;
        this.isNearTarget = isNearTarget;
        this.usableHpPotionPercent =  usableHpPotionPercent;
        this.mannerMode = mannerMode;
    }

    public short getOptions() {
        return options;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isAutoPickUpOn() {
        return pickUp;
    }

    public short getNextTargetMode() {
        return nextTargetMode;
    }

    public boolean isNearTarget() {
        return isNearTarget;
    }

    public int getUsableHpPotionPercent() {
        return usableHpPotionPercent;
    }

    public boolean isRespectfulMode() {
        return mannerMode;
    }
}
