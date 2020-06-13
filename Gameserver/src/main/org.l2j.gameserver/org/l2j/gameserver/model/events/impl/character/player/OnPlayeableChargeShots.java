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
package org.l2j.gameserver.model.events.impl.character.player;

import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

import static org.l2j.gameserver.model.events.EventType.ON_PLAYER_CHARGE_SHOTS;

/**
 * @author JoeAlisson
 */
public class OnPlayeableChargeShots implements IBaseEvent {

    private final Playable playable;
    private final ShotType shotType;
    private final boolean blessed;

    public OnPlayeableChargeShots(Playable playable, ShotType shotType, boolean blessed) {
        this.playable = playable;
        this.shotType = shotType;
        this.blessed = blessed;
    }

    public Playable getPlayable() {
        return playable;
    }

    public ShotType getShotType() {
        return shotType;
    }

    public boolean isBlessed() {
        return blessed;
    }

    @Override
    public EventType getType() {
        return ON_PLAYER_CHARGE_SHOTS;
    }
}
