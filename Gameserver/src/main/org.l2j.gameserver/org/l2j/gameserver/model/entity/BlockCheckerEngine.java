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
package org.l2j.gameserver.model.entity;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.datatables.SpawnTable;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.enums.Team;
import org.l2j.gameserver.instancemanager.HandysBlockCheckerManager;
import org.l2j.gameserver.model.ArenaParticipantsHolder;
import org.l2j.gameserver.model.Spawn;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Block;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.item.container.PlayerInventory;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.*;
import org.l2j.gameserver.world.zone.ZoneType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;


/**
 * @author BiggBoss
 */
public final class BlockCheckerEngine {
    protected static final Logger LOGGER = LoggerFactory.getLogger(BlockCheckerEngine.class);
    // The needed arena coordinates
    // Arena X: team1X, team1Y, team2X, team2Y, ArenaCenterX, ArenaCenterY
    protected static final int[][] _arenaCoordinates =
            {
                    // Arena 0 - Team 1 XY, Team 2 XY - CENTER XY
                    {
                            -58368,
                            -62745,
                            -57751,
                            -62131,
                            -58053,
                            -62417
                    },
                    // Arena 1 - Team 1 XY, Team 2 XY - CENTER XY
                    {
                            -58350,
                            -63853,
                            -57756,
                            -63266,
                            -58053,
                            -63551
                    },
                    // Arena 2 - Team 1 XY, Team 2 XY - CENTER XY
                    {
                            -57194,
                            -63861,
                            -56580,
                            -63249,
                            -56886,
                            -63551
                    },
                    // Arena 3 - Team 1 XY, Team 2 XY - CENTER XY
                    {
                            -57200,
                            -62727,
                            -56584,
                            -62115,
                            -56850,
                            -62391
                    }
            };
    // Common z coordinate
    private static final int _zCoord = -2405;
    // Default arena
    private static final byte DEFAULT_ARENA = -1;
    // The object which holds all basic members info
    protected ArenaParticipantsHolder _holder;
    // Maps to hold player of each team and his points
    protected Map<Player, Integer> _redTeamPoints = new ConcurrentHashMap<>();
    protected Map<Player, Integer> _blueTeamPoints = new ConcurrentHashMap<>();
    // The initial points of the event
    protected int _redPoints = 15;
    protected int _bluePoints = 15;
    // Current used arena
    protected int _arena = -1;
    // All blocks
    protected Set<Spawn> _spawns = ConcurrentHashMap.newKeySet();
    // Sets if the red team won the event at the end of this (used for packets)
    protected boolean _isRedWinner;
    // Time when the event starts. Used on packet sending
    protected long _startedTime;
    // List of dropped items in event (for later deletion)
    protected Set<Item> _drops = ConcurrentHashMap.newKeySet();
    // Event is started
    protected boolean _isStarted = false;
    // Event end
    protected ScheduledFuture<?> _task;
    // Preserve from exploit reward by logging out
    protected boolean _abnormalEnd = false;

    public BlockCheckerEngine(ArenaParticipantsHolder holder, int arena) {
        _holder = holder;
        if ((arena > -1) && (arena < 4)) {
            _arena = arena;
        }

        for (Player player : holder.getRedPlayers()) {
            _redTeamPoints.put(player, 0);
        }
        for (Player player : holder.getBluePlayers()) {
            _blueTeamPoints.put(player, 0);
        }
    }

    /**
     * Updates the player holder before the event starts to synchronize all info
     *
     * @param holder
     */
    public void updatePlayersOnStart(ArenaParticipantsHolder holder) {
        _holder = holder;
    }

    /**
     * Returns the current holder object of this object engine
     *
     * @return HandysBlockCheckerManager.ArenaParticipantsHolder
     */
    public ArenaParticipantsHolder getHolder() {
        return _holder;
    }

    /**
     * Will return the id of the arena used by this event
     *
     * @return false;
     */
    public int getArena() {
        return _arena;
    }

    /**
     * Returns the time when the event started
     *
     * @return long
     */
    public long getStarterTime() {
        return _startedTime;
    }

    /**
     * Returns the current red team points
     *
     * @return int
     */
    public int getRedPoints() {
        synchronized (this) {
            return _redPoints;
        }
    }

    /**
     * Returns the current blue team points
     *
     * @return int
     */
    public int getBluePoints() {
        synchronized (this) {
            return _bluePoints;
        }
    }

    /**
     * Returns the player points
     *
     * @param player
     * @param isRed
     * @return int
     */
    public int getPlayerPoints(Player player, boolean isRed) {
        if (!_redTeamPoints.containsKey(player) && !_blueTeamPoints.containsKey(player)) {
            return 0;
        }

        if (isRed) {
            return _redTeamPoints.get(player);
        }
        return _blueTeamPoints.get(player);
    }

    /**
     * Increases player points for his teams
     *
     * @param player
     * @param team
     */
    public synchronized void increasePlayerPoints(Player player, int team) {
        if (player == null) {
            return;
        }

        if (team == 0) {
            final int points = _redTeamPoints.get(player) + 1;
            _redTeamPoints.put(player, points);
            _redPoints++;
            _bluePoints--;
        } else {
            final int points = _blueTeamPoints.get(player) + 1;
            _blueTeamPoints.put(player, points);
            _bluePoints++;
            _redPoints--;
        }
    }

    /**
     * Will add a new drop into the list of dropped items
     *
     * @param item
     */
    public void addNewDrop(Item item) {
        if (item != null) {
            _drops.add(item);
        }
    }

    /**
     * Will return true if the event is already started
     *
     * @return boolean
     */
    public boolean isStarted() {
        return _isStarted;
    }

    /**
     * Will send all packets for the event members with the relation info
     *
     * @param plr
     */
    protected void broadcastRelationChanged(Player plr) {
        for (Player p : _holder.getAllPlayers()) {
            p.sendPacket(new RelationChanged(plr, plr.getRelation(p), plr.isAutoAttackable(p)));
        }
    }

    /**
     * Called when a there is an empty team. The event will end.
     */
    public void endEventAbnormally() {
        try {
            synchronized (this) {
                _isStarted = false;

                if (_task != null) {
                    _task.cancel(true);
                }

                _abnormalEnd = true;

                ThreadPool.execute(new EndEvent());
            }
        } catch (Exception e) {
            LOGGER.error("Couldnt end Block Checker event at " + _arena, e);
        }
    }

    /**
     * This inner class set ups all player and arena parameters to start the event
     */
    public class StartEvent implements Runnable {
        // In event used skills
        private final Skill _freeze;
        private final Skill _transformationRed;
        private final Skill _transformationBlue;
        // Common and unparametizer packet
        private final ExCubeGameCloseUI _closeUserInterface = ExCubeGameCloseUI.STATIC_PACKET;

        public StartEvent() {
            // Initialize all used skills
            _freeze = SkillEngine.getInstance().getSkill(6034, 1);
            _transformationRed = SkillEngine.getInstance().getSkill(6035, 1);
            _transformationBlue = SkillEngine.getInstance().getSkill(6036, 1);
        }

        /**
         * Will set up all player parameters and port them to their respective location based on their teams
         */
        private void setUpPlayers() {
            // Set current arena as being used
            HandysBlockCheckerManager.getInstance().setArenaBeingUsed(_arena);

            // Initialize packets avoiding create a new one per player
            _redPoints = _spawns.size() / 2;
            _bluePoints = _spawns.size() / 2;
            final ExCubeGameChangePoints initialPoints = new ExCubeGameChangePoints(300, _bluePoints, _redPoints);
            ExCubeGameExtendedChangePoints clientSetUp;

            for (Player player : _holder.getAllPlayers()) {
                if (player == null) {
                    continue;
                }

                // Send the secret client packet set up
                final boolean isRed = _holder.getRedPlayers().contains(player);

                clientSetUp = new ExCubeGameExtendedChangePoints(300, _bluePoints, _redPoints, isRed, player, 0);
                player.sendPacket(clientSetUp);

                player.sendPacket(ActionFailed.STATIC_PACKET);

                // Teleport Player - Array access
                // Team 0 * 2 = 0; 0 = 0, 0 + 1 = 1.
                // Team 1 * 2 = 2; 2 = 2, 2 + 1 = 3
                final int tc = _holder.getPlayerTeam(player) * 2;
                // Get x and y coordinates
                final int x = _arenaCoordinates[_arena][tc];
                final int y = _arenaCoordinates[_arena][tc + 1];
                player.teleToLocation(x, y, _zCoord);
                // Set the player team
                if (isRed) {
                    _redTeamPoints.put(player, 0);
                    player.setTeam(Team.RED);
                } else {
                    _blueTeamPoints.put(player, 0);
                    player.setTeam(Team.BLUE);
                }
                player.stopAllEffects();
                final Summon pet = player.getPet();
                if (pet != null) {
                    pet.unSummon(player);
                }
                player.getServitors().values().forEach(s -> s.unSummon(player));

                // Give the player start up effects
                // Freeze
                _freeze.applyEffects(player, player);
                // Transformation
                if (_holder.getPlayerTeam(player) == 0) {
                    _transformationRed.applyEffects(player, player);
                } else {
                    _transformationBlue.applyEffects(player, player);
                }
                // Set the current player arena
                player.setBlockCheckerArena((byte) _arena);
                player.setInsideZone(ZoneType.PVP, true);
                // Send needed packets
                player.sendPacket(initialPoints);
                player.sendPacket(_closeUserInterface);
                // ExBasicActionList
                player.sendPacket(ExBasicActionList.STATIC_PACKET);
                broadcastRelationChanged(player);
            }
        }

        @Override
        public void run() {
            // Wrong arena passed, stop event
            if (_arena == -1) {
                LOGGER.error("Couldnt set up the arena Id for the Block Checker event, cancelling event...");
                return;
            }
            _isStarted = true;
            // Spawn the blocks
            ThreadPool.execute(new SpawnRound(16, 1));
            // Start up player parameters
            setUpPlayers();
            // Set the started time
            _startedTime = System.currentTimeMillis() + 300000;
        }
    }

    /**
     * This class spawns the second round of boxes and schedules the event end
     */
    private class SpawnRound implements Runnable {
        int _numOfBoxes;
        int _round;

        SpawnRound(int numberOfBoxes, int round) {
            _numOfBoxes = numberOfBoxes;
            _round = round;
        }

        @Override
        public void run() {
            if (!_isStarted) {
                return;
            }

            switch (_round) {
                case 1: // Schedule second spawn round
                {
                    _task = ThreadPool.schedule(new SpawnRound(20, 2), 60000);
                    break;
                }
                case 2: // Schedule third spawn round
                {
                    _task = ThreadPool.schedule(new SpawnRound(14, 3), 60000);
                    break;
                }
                case 3: // Schedule Event End Count Down
                {
                    _task = ThreadPool.schedule(new EndEvent(), 180000);
                    break;
                }
            }
            // random % 2, if == 0 will spawn a red block
            // if != 0, will spawn a blue block
            byte random = 2;
            // common template
            final NpcTemplate template = NpcData.getInstance().getTemplate(18672);
            // Spawn blocks
            try {
                // Creates 50 new blocks
                for (int i = 0; i < _numOfBoxes; i++) {
                    final Spawn spawn = new Spawn(template);
                    spawn.setXYZ(_arenaCoordinates[_arena][4] + Rnd.get(-400, 400), _arenaCoordinates[_arena][5] + Rnd.get(-400, 400), _zCoord);
                    spawn.setAmount(1);
                    spawn.setHeading(1);
                    spawn.setRespawnDelay(1);
                    SpawnTable.getInstance().addNewSpawn(spawn, false);
                    spawn.init();
                    final Block block = (Block) spawn.getLastSpawn();
                    // switch color
                    if ((random % 2) == 0) {
                        block.setRed(true);
                    } else {
                        block.setRed(false);
                    }

                    block.disableCoreAI(true);
                    _spawns.add(spawn);
                    random++;
                }
            } catch (Exception e) {
                LOGGER.warn(": " + e.getMessage());
            }

            // Spawn the block carrying girl
            if ((_round == 1) || (_round == 2)) {
                try {
                    final Spawn girlSpawn = new Spawn(18676);
                    girlSpawn.setXYZ(_arenaCoordinates[_arena][4] + Rnd.get(-400, 400), _arenaCoordinates[_arena][5] + Rnd.get(-400, 400), _zCoord);
                    girlSpawn.setAmount(1);
                    girlSpawn.setHeading(1);
                    girlSpawn.setRespawnDelay(1);
                    SpawnTable.getInstance().addNewSpawn(girlSpawn, false);
                    girlSpawn.init();
                    // Schedule his deletion after 9 secs of spawn
                    ThreadPool.schedule(new CarryingGirlUnspawn(girlSpawn), 9000);
                } catch (Exception e) {
                    LOGGER.warn("Couldnt Spawn Block Checker NPCs! Wrong instance type at npc table?");
                    LOGGER.warn(": " + e.getMessage());
                }
            }

            _redPoints += _numOfBoxes / 2;
            _bluePoints += _numOfBoxes / 2;

            final int timeLeft = (int) ((_startedTime - System.currentTimeMillis()) / 1000);
            final ExCubeGameChangePoints changePoints = new ExCubeGameChangePoints(timeLeft, getBluePoints(), getRedPoints());
            _holder.broadCastPacketToTeam(changePoints);
        }
    }

    private class CarryingGirlUnspawn implements Runnable {
        private final Spawn _spawn;

        protected CarryingGirlUnspawn(Spawn spawn) {
            _spawn = spawn;
        }

        @Override
        public void run() {
            if (_spawn == null) {
                LOGGER.warn("HBCE: Block Carrying Girl is null");
                return;
            }
            SpawnTable.getInstance().deleteSpawn(_spawn, false);
            _spawn.stopRespawn();
            _spawn.getLastSpawn().deleteMe();
        }
    }

    /*
     * private class CountDown implements Runnable {
     * @Override public void run() { _holder.broadCastPacketToTeam(SystemMessage.getSystemMessage(SystemMessageId.BLOCK_CHECKER_ENDS_5)); ThreadPool.schedule(new EndEvent(), 5000); } }
     */

    /**
     * This class erase all event parameters on player and port them back near Handy. Also, unspawn blocks, runs a garbage collector and set as free the used arena
     */
    protected class EndEvent implements Runnable {
        // Garbage collector and arena free setter
        private void clearMe() {
            HandysBlockCheckerManager.getInstance().clearPaticipantQueueByArenaId(_arena);
            _holder.clearPlayers();
            _blueTeamPoints.clear();
            _redTeamPoints.clear();
            HandysBlockCheckerManager.getInstance().setArenaFree(_arena);

            for (Spawn spawn : _spawns) {
                spawn.stopRespawn();
                spawn.getLastSpawn().deleteMe();
                SpawnTable.getInstance().deleteSpawn(spawn, false);
            }
            _spawns.clear();

            for (Item item : _drops) {
                // npe
                if (item == null) {
                    continue;
                }

                // a player has it, it will be deleted later
                if (!item.isSpawned() || (item.getOwnerId() != 0)) {
                    continue;
                }

                item.decayMe();
            }
            _drops.clear();
        }

        /**
         * Reward players after event. Tie - No Reward
         */
        private void rewardPlayers() {
            if (_redPoints == _bluePoints) {
                return;
            }

            _isRedWinner = _redPoints > _bluePoints;

            if (_isRedWinner) {
                rewardAsWinner(true);
                rewardAsLooser(false);
                final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.THE_C1_TEAM_HAS_WON);
                msg.addString("Red Team");
                _holder.broadCastPacketToTeam(msg);
            } else if (_bluePoints > _redPoints) {
                rewardAsWinner(false);
                rewardAsLooser(true);
                final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.THE_C1_TEAM_HAS_WON);
                msg.addString("Blue Team");
                _holder.broadCastPacketToTeam(msg);
            } else {
                rewardAsLooser(true);
                rewardAsLooser(false);
            }
        }

        /**
         * Reward the specified team as a winner team 1) Higher score - 8 extra 2) Higher score - 5 extra
         *
         * @param isRed
         */
        private void rewardAsWinner(boolean isRed) {
            final Map<Player, Integer> tempPoints = isRed ? _redTeamPoints : _blueTeamPoints;

            // Main give
            for (Entry<Player, Integer> points : tempPoints.entrySet()) {
                if (points.getKey() == null) {
                    continue;
                }

                if (points.getValue() >= 10) {
                    points.getKey().addItem("Block Checker", 13067, 2, points.getKey(), true);
                } else {
                    tempPoints.remove(points.getKey());
                }
            }

            int first = 0;
            int second = 0;
            Player winner1 = null;
            Player winner2 = null;
            for (Entry<Player, Integer> entry : tempPoints.entrySet()) {
                final Player pc = entry.getKey();
                final int pcPoints = entry.getValue();
                if (pcPoints > first) {
                    // Move old data
                    second = first;
                    winner2 = winner1;
                    // Set new data
                    first = pcPoints;
                    winner1 = pc;
                } else if (pcPoints > second) {
                    second = pcPoints;
                    winner2 = pc;
                }
            }
            if (winner1 != null) {
                winner1.addItem("Block Checker", 13067, 8, winner1, true);
            }
            if (winner2 != null) {
                winner2.addItem("Block Checker", 13067, 5, winner2, true);
            }
        }

        /**
         * Will reward the looser team with the predefined rewards Player got >= 10 points: 2 coins Player got < 10 points: 0 coins
         *
         * @param isRed
         */
        private void rewardAsLooser(boolean isRed) {
            for (Entry<Player, Integer> entry : (isRed ? _redTeamPoints : _blueTeamPoints).entrySet()) {
                final Player player = entry.getKey();
                if ((player != null) && (entry.getValue() >= 10)) {
                    player.addItem("Block Checker", 13067, 2, player, true);
                }
            }
        }

        /**
         * Teleport players back, give status back and send final packet
         */
        private void setPlayersBack() {
            final ExCubeGameEnd end = new ExCubeGameEnd(_isRedWinner);

            for (Player player : _holder.getAllPlayers()) {
                if (player == null) {
                    continue;
                }

                player.stopAllEffects();
                // Remove team aura
                player.setTeam(Team.NONE);
                // Set default arena
                player.setBlockCheckerArena(DEFAULT_ARENA);
                // Remove the event item
                final PlayerInventory inv = player.getInventory();
                if (inv.getItemByItemId(13787) != null) {
                    inv.destroyItemByItemId("Handys Block Checker", 13787, inv.getInventoryItemCount(13787, 0), player, player);
                }
                if (inv.getItemByItemId(13788) != null) {
                    inv.destroyItemByItemId("Handys Block Checker", 13788, inv.getInventoryItemCount(13788, 0), player, player);
                }
                broadcastRelationChanged(player);
                // Teleport Back
                player.teleToLocation(-57478, -60367, -2370);
                player.setInsideZone(ZoneType.PVP, false);
                // Send end packet
                player.sendPacket(end);
                player.broadcastUserInfo();
            }
        }

        @Override
        public void run() {
            if (!_abnormalEnd) {
                rewardPlayers();
            }
            setPlayersBack();
            clearMe();
            _isStarted = false;
            _abnormalEnd = false;
        }
    }
}
