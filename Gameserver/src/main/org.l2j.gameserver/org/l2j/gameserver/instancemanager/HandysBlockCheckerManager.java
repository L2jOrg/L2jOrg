/*
 * Copyright © 2019 L2J Mobius
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
package org.l2j.gameserver.instancemanager;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.Team;
import org.l2j.gameserver.instancemanager.tasks.PenaltyRemoveTask;
import org.l2j.gameserver.model.ArenaParticipantsHolder;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.model.olympiad.OlympiadManager;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExCubeGameAddPlayer;
import org.l2j.gameserver.network.serverpackets.ExCubeGameChangeTeam;
import org.l2j.gameserver.network.serverpackets.ExCubeGameRemovePlayer;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.world.zone.ZoneType;

import java.util.*;

/**
 * This class manage the player add/remove, team change and event arena status,<br>
 * as the clearance of the participants list or liberate the arena.
 *
 * @author BiggBoss
 */
public final class HandysBlockCheckerManager {
    // All the participants and their team classified by arena
    private static final ArenaParticipantsHolder[] _arenaPlayers = new ArenaParticipantsHolder[4];

    // Arena votes to start the game
    private static final Map<Integer, Integer> _arenaVotes = new HashMap<>();

    // Arena Status, True = is being used, otherwise, False
    private static final Map<Integer, Boolean> _arenaStatus = new HashMap<>();

    // Registration request penalty (10 seconds)
    protected static Set<Integer> _registrationPenalty = Collections.synchronizedSet(new HashSet<>());

    private HandysBlockCheckerManager() {
        // Initialize arena status
        _arenaStatus.put(0, false);
        _arenaStatus.put(1, false);
        _arenaStatus.put(2, false);
        _arenaStatus.put(3, false);

        // Initialize arena votes
        _arenaVotes.put(0, 0);
        _arenaVotes.put(1, 0);
        _arenaVotes.put(2, 0);
        _arenaVotes.put(3, 0);
    }

    /**
     * Return the number of event-start votes for the specified arena id
     *
     * @param arenaId
     * @return int (number of votes)
     */
    public synchronized int getArenaVotes(int arenaId) {
        return _arenaVotes.get(arenaId);
    }

    /**
     * Add a new vote to start the event for the specified arena id
     *
     * @param arena
     */
    public synchronized void increaseArenaVotes(int arena) {
        final int newVotes = _arenaVotes.get(arena) + 1;
        final ArenaParticipantsHolder holder = _arenaPlayers[arena];

        if ((newVotes > (holder.getAllPlayers().size() / 2)) && !holder.getEvent().isStarted()) {
            clearArenaVotes(arena);
            if ((holder.getBlueTeamSize() == 0) || (holder.getRedTeamSize() == 0)) {
                return;
            }
            if (Config.HBCE_FAIR_PLAY) {
                holder.checkAndShuffle();
            }
            ThreadPool.execute(holder.getEvent().new StartEvent());
        } else {
            _arenaVotes.put(arena, newVotes);
        }
    }

    /**
     * Will clear the votes queue (of event start) for the specified arena id
     *
     * @param arena
     */
    public synchronized void clearArenaVotes(int arena) {
        _arenaVotes.put(arena, 0);
    }

    /**
     * Returns the players holder
     *
     * @param arena
     * @return ArenaParticipantsHolder
     */
    public ArenaParticipantsHolder getHolder(int arena) {
        return _arenaPlayers[arena];
    }

    /**
     * Initializes the participants holder
     */
    public void startUpParticipantsQueue() {
        for (int i = 0; i < 4; ++i) {
            _arenaPlayers[i] = new ArenaParticipantsHolder(i);
        }
    }

    /**
     * Add the player to the specified arena (through the specified arena manager) and send the needed server -> client packets
     *
     * @param player
     * @param arenaId
     * @return
     */
    public boolean addPlayerToArena(Player player, int arenaId) {
        final ArenaParticipantsHolder holder = _arenaPlayers[arenaId];

        synchronized (holder) {
            boolean isRed;

            for (int i = 0; i < 4; i++) {
                if (_arenaPlayers[i].getAllPlayers().contains(player)) {
                    final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.C1_YOU_HAVE_ALREADY_REGISTERED_FOR_THE_MATCH);
                    msg.addString(player.getName());
                    player.sendPacket(msg);
                    return false;
                }
            }

            if (player.isOnEvent() || player.isInOlympiadMode()) {
                player.sendMessage("Couldnt register you due other event participation");
                return false;
            }

            if (OlympiadManager.getInstance().isRegistered(player)) {
                OlympiadManager.getInstance().unRegisterNoble(player);
                player.sendPacket(SystemMessageId.APPLICANTS_FOR_THE_OLYMPIAD_UNDERGROUND_COLISEUM_OR_KRATEI_S_CUBE_MATCHES_CANNOT_REGISTER);
            }

            // if(UnderGroundColiseum.getInstance().isRegisteredPlayer(player))
            // {
            // UngerGroundColiseum.getInstance().removeParticipant(player);
            // player.sendPacket(SystemMessageId.APPLICANTS_FOR_THE_OLYMPIAD_UNDERGROUND_COLISEUM_OR_KRATEI_S_CUBE_MATCHES_CANNOT_REGISTER));
            // }
            // if(KrateiCubeManager.getInstance().isRegisteredPlayer(player))
            // {
            // KrateiCubeManager.getInstance().removeParticipant(player);
            // player.sendPacket(SystemMessageId.APPLICANTS_FOR_THE_OLYMPIAD_UNDERGROUND_COLISEUM_OR_KRATEI_S_CUBE_MATCHES_CANNOT_REGISTER));
            // }

            if (_registrationPenalty.contains(player.getObjectId())) {
                player.sendPacket(SystemMessageId.YOU_MUST_WAIT_10_SECONDS_BEFORE_ATTEMPTING_TO_REGISTER_AGAIN);
                return false;
            }

            if (holder.getBlueTeamSize() < holder.getRedTeamSize()) {
                holder.addPlayer(player, 1);
                isRed = false;
            } else {
                holder.addPlayer(player, 0);
                isRed = true;
            }
            holder.broadCastPacketToTeam(new ExCubeGameAddPlayer(player, isRed));
            return true;
        }
    }

    /**
     * Will remove the specified player from the specified team and arena and will send the needed packet to all his team mates / enemy team mates
     *
     * @param player
     * @param arenaId
     * @param team
     */
    public void removePlayer(Player player, int arenaId, int team) {
        final ArenaParticipantsHolder holder = _arenaPlayers[arenaId];
        synchronized (holder) {
            final boolean isRed = team == 0;

            holder.removePlayer(player, team);
            holder.broadCastPacketToTeam(new ExCubeGameRemovePlayer(player, isRed));

            // End event if theres an empty team
            final int teamSize = isRed ? holder.getRedTeamSize() : holder.getBlueTeamSize();
            if (teamSize == 0) {
                holder.getEvent().endEventAbnormally();
            }

            _registrationPenalty.add(player.getObjectId());
            schedulePenaltyRemoval(player.getObjectId());
        }
    }

    /**
     * Will change the player from one team to other (if possible) and will send the needed packets
     *
     * @param player
     * @param arena
     * @param team
     */
    public void changePlayerToTeam(Player player, int arena, int team) {
        final ArenaParticipantsHolder holder = _arenaPlayers[arena];

        synchronized (holder) {
            final boolean isFromRed = holder.getRedPlayers().contains(player);

            if (isFromRed && (holder.getBlueTeamSize() == 6)) {
                player.sendMessage("The team is full");
                return;
            } else if (!isFromRed && (holder.getRedTeamSize() == 6)) {
                player.sendMessage("The team is full");
                return;
            }

            final int futureTeam = isFromRed ? 1 : 0;
            holder.addPlayer(player, futureTeam);

            if (isFromRed) {
                holder.removePlayer(player, 0);
            } else {
                holder.removePlayer(player, 1);
            }
            holder.broadCastPacketToTeam(new ExCubeGameChangeTeam(player, isFromRed));
        }
    }

    /**
     * Will erase all participants from the specified holder
     *
     * @param arenaId
     */
    public synchronized void clearPaticipantQueueByArenaId(int arenaId) {
        _arenaPlayers[arenaId].clearPlayers();
    }

    /**
     * Returns true if arena is holding an event at this momment
     *
     * @param arenaId
     * @return boolean
     */
    public boolean arenaIsBeingUsed(int arenaId) {
        if ((arenaId < 0) || (arenaId > 3)) {
            return false;
        }
        return _arenaStatus.get(arenaId);
    }

    /**
     * Set the specified arena as being used
     *
     * @param arenaId
     */
    public void setArenaBeingUsed(int arenaId) {
        _arenaStatus.put(arenaId, true);
    }

    /**
     * Set as free the specified arena for future events
     *
     * @param arenaId
     */
    public void setArenaFree(int arenaId) {
        _arenaStatus.put(arenaId, false);
    }

    /**
     * Called when played logs out while participating in Block Checker Event
     *
     * @param player
     */
    public void onDisconnect(Player player) {
        final int arena = player.getBlockCheckerArena();
        final int team = getHolder(arena).getPlayerTeam(player);
        getInstance().removePlayer(player, arena, team);
        if (player.getTeam() != Team.NONE) {
            player.stopAllEffects();
            // Remove team aura
            player.setTeam(Team.NONE);

            // Remove the event items
            final PlayerInventory inv = player.getInventory();

            if (inv.getItemByItemId(13787) != null) {
                final long count = inv.getInventoryItemCount(13787, 0);
                inv.destroyItemByItemId("Handys Block Checker", 13787, count, player, player);
            }
            if (inv.getItemByItemId(13788) != null) {
                final long count = inv.getInventoryItemCount(13788, 0);
                inv.destroyItemByItemId("Handys Block Checker", 13788, count, player, player);
            }
            player.setInsideZone(ZoneType.PVP, false);
            // Teleport Back
            player.teleToLocation(-57478, -60367, -2370);
        }
    }

    public void removePenalty(int objectId) {
        _registrationPenalty.remove(objectId);
    }

    private void schedulePenaltyRemoval(int objId) {
        ThreadPool.schedule(new PenaltyRemoveTask(objId), 10000);
    }

    public static HandysBlockCheckerManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final HandysBlockCheckerManager INSTANCE = new HandysBlockCheckerManager();
    }
}
