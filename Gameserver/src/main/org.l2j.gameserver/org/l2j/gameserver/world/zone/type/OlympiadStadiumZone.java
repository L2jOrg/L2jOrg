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

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadMatchEnd;
import org.l2j.gameserver.util.GameXmlReader;
import org.l2j.gameserver.world.zone.Zone;
import org.l2j.gameserver.world.zone.ZoneFactory;
import org.w3c.dom.Node;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayable;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * An olympiad stadium
 *
 * @author durgus, DS
 */
public class OlympiadStadiumZone extends SpawnZone {

    private OlympiadStadiumZone(int id) {
        super(id);
    }

    @Override
    protected final void onEnter(Creature creature) {
        if (isPlayable(creature)) {
            final Player player = creature.getActingPlayer();
            if (nonNull(player)) {
                if (!player.isInOlympiadMode()) {
                    ThreadPool.execute(new KickPlayer(player));
                } else {
                    final Summon pet = player.getPet();
                    if (nonNull(pet)) {
                        pet.unSummon(player);
                    }
                }
            }
        }
    }

    @Override
    protected final void onExit(Creature creature) {
        if (isPlayer(creature)) {
            creature.sendPacket(ExOlympiadMatchEnd.STATIC_PACKET);
        }
    }

    private record KickPlayer(Player player) implements Runnable {

        @Override
        public void run() {
            player.getServitors().values().forEach(s -> s.unSummon(player));
            player.teleToLocation(TeleportWhereType.TOWN, null);
        }
    }

    public static class Factory implements ZoneFactory {

        @Override
        public Zone create(int id, Node zoneNode, GameXmlReader reader) {
            return new OlympiadStadiumZone(id);
        }

        @Override
        public String type() {
            return "olympiad-stadium";
        }
    }
}
