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
package org.l2j.gameserver.engine.vip;

/**
 * @author JoeAlisson
 */
class VipInfo {

    private final byte tier;
    private final long pointsRequired;
    private final long pointsDepreciated;
    private float silverCoinChance;
    private float rustyCoinChance;
    private int skill;

    VipInfo(byte tier, long pointsRequired, long pointsDepreciated) {
        this.tier = tier;
        this.pointsRequired = pointsRequired;
        this.pointsDepreciated = pointsDepreciated;
    }

    byte getTier() {
        return tier;
    }

    void setSilverCoinChance(float silverCoinChance) {
        this.silverCoinChance = silverCoinChance;
    }

    float getSilverCoinChance() {
        return silverCoinChance;
    }

    void setRustyCoinChance(float rustyCoinChance) {
        this.rustyCoinChance = rustyCoinChance;
    }

    float getRustyCoinChance() {
        return rustyCoinChance;
    }

    long getPointsRequired() {
        return pointsRequired;
    }

    long getPointsDepreciated() {
        return pointsDepreciated;
    }

    int getSkill() {
        return skill;
    }

    void setSkill(int skill) {
        this.skill = skill;
    }
}
