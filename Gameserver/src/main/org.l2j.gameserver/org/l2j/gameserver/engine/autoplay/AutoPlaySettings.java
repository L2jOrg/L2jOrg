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
