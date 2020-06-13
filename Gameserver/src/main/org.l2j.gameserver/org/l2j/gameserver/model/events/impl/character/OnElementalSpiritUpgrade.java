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
package org.l2j.gameserver.model.events.impl.character;

import org.l2j.gameserver.api.elemental.ElementalSpirit;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.IBaseEvent;

public class OnElementalSpiritUpgrade implements IBaseEvent {

    private final ElementalSpirit spirit;
    private final Player player;

    public OnElementalSpiritUpgrade(Player player, ElementalSpirit spirit) {
        this.player = player;
        this.spirit = spirit;
    }

    public ElementalSpirit getSpirit() {
        return spirit;
    }

    public Player getPlayer() {
        return player;
    }


    @Override
    public EventType getType() {
        return EventType.ON_ELEMENTAL_SPIRIT_UPGRADE;
    }
}
