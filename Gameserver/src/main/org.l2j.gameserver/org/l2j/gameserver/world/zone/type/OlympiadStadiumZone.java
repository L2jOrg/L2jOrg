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
package org.l2j.gameserver.world.zone.type;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.PcCondOverride;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.TeleportWhereType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.olympiad.OlympiadGameTask;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadMatchEnd;
import org.l2j.gameserver.world.zone.AbstractZoneSettings;
import org.l2j.gameserver.world.zone.ZoneManager;
import org.l2j.gameserver.world.zone.ZoneType;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isPlayable;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * An olympiad stadium
 *
 * @author durgus, DS
 */
public class OlympiadStadiumZone extends SpawnZone {
    private final List<Door> doors = new ArrayList<>(2);
    private final List<Spawn> buffers = new ArrayList<>(2);
    private final List<Location> spectatorLocations = new ArrayList<>(1);

    public OlympiadStadiumZone(int id) {
        super(id);
        AbstractZoneSettings settings = ZoneManager.getSettings(getName());
        if (settings == null) {
            settings = new Settings();
        }
        setSettings(settings);
    }

    @Override
    public Settings getSettings() {
        return (Settings) super.getSettings();
    }

    @Override
    public void parseLoc(int x, int y, int z, String type) {
        if (nonNull(type) && type.equals("spectatorSpawn")) {
            spectatorLocations.add(new Location(x, y, z));
        } else {
            super.parseLoc(x, y, z, type);
        }
    }

    @Override
    protected final void onEnter(Creature creature) {
        OlympiadGameTask task;
        if (nonNull(task = getSettings().getOlympiadTask()) && task.isBattleStarted()) {
            creature.setInsideZone(ZoneType.PVP, true);
            if (isPlayer(creature)) {
                creature.sendPacket(SystemMessageId.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
                task.getGame().sendOlympiadInfo(creature);
            }
        }

        if (isPlayable(creature)) {
            final Player player = creature.getActingPlayer();
            if (nonNull(player)) {
                // only participants, observers and GMs allowed
                if (!player.canOverrideCond(PcCondOverride.ZONE_CONDITIONS) && !player.isInOlympiadMode() && !player.inObserverMode()) {
                    ThreadPool.execute(new KickPlayer(player));
                } else {
                    // check for pet
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
        OlympiadGameTask task;
        if ( nonNull(task = getSettings().getOlympiadTask()) && task.isBattleStarted()) {
            creature.setInsideZone(ZoneType.PVP, false);
            if (isPlayer(creature)) {
                creature.sendPacket(SystemMessageId.YOU_HAVE_LEFT_A_COMBAT_ZONE);
                creature.sendPacket(ExOlympiadMatchEnd.STATIC_PACKET);
            }
        }
    }

    public List<Door> getDoors() {
        return doors;
    }

    public List<Spawn> getBuffers() {
        return buffers;
    }

    public List<Location> getSpectatorSpawns() {
        return spectatorLocations;
    }

    private static final class KickPlayer implements Runnable {
        private final Player player;

        KickPlayer(Player player) {
            this.player = player;
        }

        @Override
        public void run() {
            player.getServitors().values().forEach(s -> s.unSummon(player));
            player.teleToLocation(TeleportWhereType.TOWN, null);
        }
    }

    public static final class Settings extends AbstractZoneSettings {
        private OlympiadGameTask _task = null;

        protected Settings() {
        }

        public OlympiadGameTask getOlympiadTask() {
            return _task;
        }

        @Override
        public void clear() {
            _task = null;
        }
    }
}
