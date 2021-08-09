/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2021 L2JOrg
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
package org.l2j.gameserver.engine.olympiad;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;

import static org.l2j.commons.util.Util.emptyIfNullOrElse;
import static org.l2j.commons.util.Util.zeroIfNullOrElse;

/**
 * @author JIV
 * @author JoeAlisson
 */
public class OlympiadResultInfo {

    private final Player player;
    private final int damage;

    private int currentPoints;
    private int diffPoints;

    private OlympiadResultInfo(Player player, double damage) {
        this.player = player;
        this.damage = (int) damage;
    }

    public String getName() {
        return player.getAppearance().getVisibleName();
    }

    public String getClanName() {
        return emptyIfNullOrElse(player.getClan(), Clan::getName);
    }

    public int getClanId() {
        return zeroIfNullOrElse(player.getClan(), Clan::getId);
    }

    public int getClassId() {
        return player.getClassId().getId();
    }

    public int getDamage() {
        return damage;
    }

    public int getCurrentPoints() {
        return currentPoints;
    }

    public int getDiffPoints() {
        return diffPoints;
    }

    public Player getPlayer() {
        return player;
    }

    public void updatePoints(int currentPoints, int diffPoints) {
        this.currentPoints = currentPoints;
        this.diffPoints = diffPoints;
    }

    public static OlympiadResultInfo of(Player player, double damage) {
        return new OlympiadResultInfo(player, damage);
    }
}