/*
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
package org.l2j.gameserver.world.zone.type;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPeaceZoneEnter;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerPeaceZoneExit;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneFactory;
import org.l2j.gameserver.world.zone.ZoneType;
import org.w3c.dom.Node;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * A Peace Zone
 *
 * @author durgus
 */
public class PeaceZone extends Zone {

    private final boolean allowStore;

    private PeaceZone(int id, boolean allowStore) {
        super(id);
        this.allowStore = allowStore;
    }

    @Override
    protected void onEnter(Creature creature) {
        if (!isEnabled()) {
            return;
        }

        if (isPlayer(creature)) {
            final Player player = creature.getActingPlayer();
            // PVP possible during siege, now for siege participants only
            // Could also check if this town is in siege, or if any siege is going on
            if ((player.getSiegeState() != 0) && (Config.PEACE_ZONE_MODE == 1)) {
                return;
            }
        }

        if (Config.PEACE_ZONE_MODE != 2) {
            creature.setInsideZone(ZoneType.PEACE, true);
            if(isPlayer(creature)) {
                EventDispatcher.getInstance().notifyEventAsync(new OnPlayerPeaceZoneEnter(creature.getActingPlayer(), this), creature);
            }
        }

        if (!allowStore) {
            creature.setInsideZone(ZoneType.NO_STORE, true);
        }
    }

    @Override
    protected void onExit(Creature creature) {
        if (Config.PEACE_ZONE_MODE != 2) {
            creature.setInsideZone(ZoneType.PEACE, false);
            if(isPlayer(creature)) {
                EventDispatcher.getInstance().notifyEventAsync(new OnPlayerPeaceZoneExit(creature.getActingPlayer()), creature);
            }
        }

        if (!allowStore) {
            creature.setInsideZone(ZoneType.NO_STORE, false);
        }
    }

    @Override
    public void setEnabled(boolean state) {
        super.setEnabled(state);
        if (state) {
            forEachPlayer(this::revalidateZone);
        } else {
            forEachCreature(this::removeCreature);
        }
    }

    private void revalidateZone(Player player) {
        revalidateInZone(player);

        if(nonNull(player.getPet())) {
            revalidateInZone(player.getPet());
        }

        player.getServitors().values().forEach(this::revalidateInZone);
    }

    public static class Factory implements ZoneFactory {

        @Override
        public Zone create(int id, Node zoneNode, GameXmlReader reader) {
            var allowStore = reader.parseBoolean(zoneNode.getAttributes(), "allow-store");
            return new PeaceZone(id, allowStore);
        }

        @Override
        public String type() {
            return "peace";
        }
    }
}
