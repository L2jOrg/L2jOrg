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

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.entity.Duel;
import org.l2j.gameserver.network.serverpackets.ServerPacket;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public final class DuelManager {
    private static final int[] ARENAS =
            {
                    147, // OlympiadGrassyArena.xml
                    148, // OlympiadThreeBridgesArena.xml
                    149, // OlympiadHerossVestigesArena.xml
                    150, // OlympiadOrbisArena.xml
            };
    private final Map<Integer, Duel> _duels = new ConcurrentHashMap<>();
    private final AtomicInteger _currentDuelId = new AtomicInteger();

    private DuelManager() {
    }

    public Duel getDuel(int duelId) {
        return _duels.get(duelId);
    }

    public void addDuel(Player playerA, Player playerB, int partyDuel) {
        if ((playerA == null) || (playerB == null)) {
            return;
        }

        // return if a player has PvPFlag
        final String engagedInPvP = "The duel was canceled because a duelist engaged in PvP combat.";
        if (partyDuel == 1) {
            boolean playerInPvP = false;
            for (Player temp : playerA.getParty().getMembers()) {
                if (temp.getPvpFlag() != 0) {
                    playerInPvP = true;
                    break;
                }
            }
            if (!playerInPvP) {
                for (Player temp : playerB.getParty().getMembers()) {
                    if (temp.getPvpFlag() != 0) {
                        playerInPvP = true;
                        break;
                    }
                }
            }
            // A player has PvP flag
            if (playerInPvP) {
                for (Player temp : playerA.getParty().getMembers()) {
                    temp.sendMessage(engagedInPvP);
                }
                for (Player temp : playerB.getParty().getMembers()) {
                    temp.sendMessage(engagedInPvP);
                }
                return;
            }
        } else if ((playerA.getPvpFlag() != 0) || (playerB.getPvpFlag() != 0)) {
            playerA.sendMessage(engagedInPvP);
            playerB.sendMessage(engagedInPvP);
            return;
        }
        final int duelId = _currentDuelId.incrementAndGet();
        _duels.put(duelId, new Duel(playerA, playerB, partyDuel, duelId));
    }

    public void removeDuel(Duel duel) {
        _duels.remove(duel.getId());
    }

    public void doSurrender(Player player) {
        if ((player == null) || !player.isInDuel()) {
            return;
        }
        final Duel duel = getDuel(player.getDuelId());
        duel.doSurrender(player);
    }

    /**
     * Updates player states.
     *
     * @param player - the dying player
     */
    public void onPlayerDefeat(Player player) {
        if ((player == null) || !player.isInDuel()) {
            return;
        }
        final Duel duel = getDuel(player.getDuelId());
        if (duel != null) {
            duel.onPlayerDefeat(player);
        }
    }

    /**
     * Registers a buff which will be removed if the duel ends
     *
     * @param player
     * @param buff
     */
    public void onBuff(Player player, Skill buff) {
        if ((player == null) || !player.isInDuel() || (buff == null)) {
            return;
        }
        final Duel duel = getDuel(player.getDuelId());
        if (duel != null) {
            duel.onBuff(player, buff);
        }
    }

    /**
     * Removes player from duel.
     *
     * @param player - the removed player
     */
    public void onRemoveFromParty(Player player) {
        if ((player == null) || !player.isInDuel()) {
            return;
        }
        final Duel duel = getDuel(player.getDuelId());
        if (duel != null) {
            duel.onRemoveFromParty(player);
        }
    }

    /**
     * Broadcasts a packet to the team opposing the given player.
     *
     * @param player
     * @param packet
     */
    public void broadcastToOppositTeam(Player player, ServerPacket packet) {
        if ((player == null) || !player.isInDuel()) {
            return;
        }
        final Duel duel = getDuel(player.getDuelId());
        if (duel == null) {
            return;
        }
        if ((duel.getPlayerA() == null) || (duel.getPlayerB() == null)) {
            return;
        }

        if (duel.getPlayerA() == player) {
            duel.broadcastToTeam2(packet);
        } else if (duel.getPlayerB() == player) {
            duel.broadcastToTeam1(packet);
        } else if (duel.isPartyDuel()) {
            if ((duel.getPlayerA().getParty() != null) && duel.getPlayerA().getParty().getMembers().contains(player)) {
                duel.broadcastToTeam2(packet);
            } else if ((duel.getPlayerB().getParty() != null) && duel.getPlayerB().getParty().getMembers().contains(player)) {
                duel.broadcastToTeam1(packet);
            }
        }
    }

    /**
     * Gets new a random Olympiad Stadium instance name.
     *
     * @return an instance name
     */
    public int getDuelArena() {
        return ARENAS[Rnd.get(ARENAS.length)];
    }

    public static DuelManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final DuelManager INSTANCE = new DuelManager();
    }
}