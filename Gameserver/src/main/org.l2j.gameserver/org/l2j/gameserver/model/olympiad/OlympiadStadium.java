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
package org.l2j.gameserver.model.olympiad;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.instancemanager.InstanceManager;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ServerPacket;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadMatchEnd;
import org.l2j.gameserver.network.serverpackets.olympiad.ExOlympiadUserInfo;
import org.l2j.gameserver.world.zone.ZoneType;
import org.l2j.gameserver.world.zone.type.OlympiadStadiumZone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author JIV
 */
public class OlympiadStadium {
    private static final Logger LOGGER = LoggerFactory.getLogger(OlympiadStadium.class);
    private final OlympiadStadiumZone _zone;
    private final Instance _instance;
    private final List<Spawn> _buffers;
    private OlympiadGameTask _task = null;

    protected OlympiadStadium(OlympiadStadiumZone olyzone, int stadium) {
        _zone = olyzone;
        _instance = InstanceManager.getInstance().createInstance(olyzone.getInstanceTemplateId(), null);
        _buffers = _instance.getNpcs().stream().map(Npc::getSpawn).collect(Collectors.toList());
        _buffers.stream().map(Spawn::getLastSpawn).forEach(Npc::decayMe);
    }

    public OlympiadStadiumZone getZone() {
        return _zone;
    }

    public final void registerTask(OlympiadGameTask task) {
        _task = task;
    }

    public OlympiadGameTask getTask() {
        return _task;
    }

    public Instance getInstance() {
        return _instance;
    }

    public final void openDoors() {
        _instance.getDoors().forEach(Door::openMe);
    }

    public final void closeDoors() {
        _instance.getDoors().forEach(Door::closeMe);
    }

    public final void spawnBuffers() {
        _buffers.forEach(Spawn::startRespawn);
        _buffers.forEach(Spawn::doSpawn);
    }

    public final void deleteBuffers() {
        _buffers.forEach(Spawn::stopRespawn);
        _buffers.stream().map(Spawn::getLastSpawn).filter(Objects::nonNull).forEach(Npc::deleteMe);
    }

    public final void broadcastStatusUpdate(Player player) {
        final ExOlympiadUserInfo packet = new ExOlympiadUserInfo(player);
        for (Player target : _instance.getPlayers()) {
            if (target.inObserverMode() || (target.getOlympiadSide() != player.getOlympiadSide())) {
                target.sendPacket(packet);
            }
        }
    }

    public final void broadcastPacket(ServerPacket packet) {
        _instance.broadcastPacket(packet);
    }

    public final void broadcastPacketToObservers(ServerPacket packet) {
        for (Player target : _instance.getPlayers()) {
            if (target.inObserverMode()) {
                target.sendPacket(packet);
            }
        }
    }

    public final void updateZoneStatusForCharactersInside() {
        if (_task == null) {
            return;
        }

        final boolean battleStarted = _task.isBattleStarted();
        final SystemMessage sm;
        if (battleStarted) {
            sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_ENTERED_A_COMBAT_ZONE);
        } else {
            sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_HAVE_LEFT_A_COMBAT_ZONE);
        }

        for (Player player : _instance.getPlayers()) {
            if (player.inObserverMode()) {
                return;
            }

            if (battleStarted) {
                player.setInsideZone(ZoneType.PVP, true);
                player.sendPacket(sm);
            } else {
                player.setInsideZone(ZoneType.PVP, false);
                player.sendPacket(sm);
                player.sendPacket(ExOlympiadMatchEnd.STATIC_PACKET);
            }
        }
    }

    public final void updateZoneInfoForObservers() {
        if (_task == null) {
            return;
        }

        for (Player player : _instance.getPlayers()) {
            if (!player.inObserverMode()) {
                return;
            }

            final OlympiadGameTask nextArena = OlympiadGameManager.getInstance().getOlympiadTask(player.getOlympiadGameId());
            final List<Location> spectatorSpawns = nextArena.getStadium().getZone().getSpectatorSpawns();
            if (spectatorSpawns.isEmpty()) {
                LOGGER.warn(": Zone: " + nextArena.getStadium().getZone() + " doesn't have specatator spawns defined!");
                return;
            }
            final Location loc = spectatorSpawns.get(Rnd.get(spectatorSpawns.size()));
            player.enterOlympiadObserverMode(loc, player.getOlympiadGameId());
        }
    }
}