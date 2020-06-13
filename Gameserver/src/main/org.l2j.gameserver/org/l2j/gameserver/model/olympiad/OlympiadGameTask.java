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

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * @author DS
 */
public final class OlympiadGameTask implements Runnable {
    protected static final Logger LOGGER = LoggerFactory.getLogger(OlympiadGameTask.class);

    private static final int[] TELEPORT_TO_ARENA_TIMES =
            {
                    120,
                    60,
                    30,
                    15,
                    10,
                    5,
                    4,
                    3,
                    2,
                    1,
                    0
            };

    private static final int[] BATTLE_START_TIME_FIRST =
            {
                    60,
                    55,
                    50,
                    40,
                    30,
                    20,
                    10,
                    0
            };

    private static final int[] BATTLE_START_TIME_SECOND =
            {
                    10,
                    5,
                    4,
                    3,
                    2,
                    1,
                    0
            };

    private static final int[] BATTLE_END_TIME_SECOND =
            {
                    120,
                    60,
                    30,
                    10,
                    5
            };

    private static final int[] TELEPORT_TO_TOWN_TIMES =
            {
                    40,
                    30,
                    20,
                    10,
                    5,
                    4,
                    3,
                    2,
                    1,
                    0
            };

    private final OlympiadStadium _stadium;
    private AbstractOlympiadGame _game;
    private GameState _state = GameState.IDLE;
    private boolean _needAnnounce = false;
    private int _countDown = 0;

    public OlympiadGameTask(OlympiadStadium stadium) {
        _stadium = stadium;
        _stadium.registerTask(this);
    }

    public final boolean isRunning() {
        return _state != GameState.IDLE;
    }

    public final boolean isGameStarted() {
        return (_state.ordinal() >= GameState.GAME_STARTED.ordinal()) && (_state.ordinal() <= GameState.CLEANUP.ordinal());
    }

    public final boolean isBattleStarted() {
        return _state == GameState.BATTLE_IN_PROGRESS;
    }

    public final boolean isBattleFinished() {
        return _state == GameState.TELEPORT_TO_TOWN;
    }

    public final boolean needAnnounce() {
        if (_needAnnounce) {
            _needAnnounce = false;
            return true;
        }
        return false;
    }

    public final OlympiadStadium getStadium() {
        return _stadium;
    }

    public final AbstractOlympiadGame getGame() {
        return _game;
    }

    public final void attachGame(AbstractOlympiadGame game) {
        if ((game != null) && (_state != GameState.IDLE)) {
            LOGGER.warn("Attempt to overwrite non-finished game in state " + _state);
            return;
        }

        _game = game;
        _state = GameState.BEGIN;
        _needAnnounce = false;
        ThreadPool.execute(this);
    }

    @Override
    public final void run() {
        try {
            int delay = 1; // schedule next call after 1s
            switch (_state) {
                // Game created
                case BEGIN: {
                    _state = GameState.TELEPORT_TO_ARENA;
                    _countDown = Config.ALT_OLY_WAIT_TIME;
                    break;
                }
                // Teleport to arena countdown
                case TELEPORT_TO_ARENA: {
                    if (_countDown > 0) {
                        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_WILL_BE_MOVED_TO_THE_OLYMPIAD_STADIUM_IN_S1_SECOND_S);
                        sm.addInt(_countDown);
                        _game.broadcastPacket(sm);
                    }

                    if (_countDown == 1) {
                        _game.untransformPlayers();
                    }

                    delay = getDelay(TELEPORT_TO_ARENA_TIMES);
                    if (_countDown <= 0) {
                        _state = GameState.GAME_STARTED;
                    }
                    break;
                }
                // Game start, port players to arena
                case GAME_STARTED: {
                    if (!startGame()) {
                        _state = GameState.GAME_STOPPED;
                        break;
                    }

                    _state = GameState.BATTLE_COUNTDOWN_FIRST;
                    _countDown = BATTLE_START_TIME_FIRST[0];
                    _stadium.updateZoneInfoForObservers(); // TODO lion temp hack for remove old info from client about prevoius match
                    delay = 5;
                    break;
                }
                // Battle start countdown, first part (60-10)
                case BATTLE_COUNTDOWN_FIRST: {
                    if (_countDown > 0) {
                        if (_countDown == 55) // 55sec
                        {
                            _game.healPlayers();
                        } else {
                            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_MATCH_WILL_START_IN_S1_SECOND_S);
                            sm.addInt(_countDown);
                            _stadium.broadcastPacket(sm);
                        }
                    }

                    delay = getDelay(BATTLE_START_TIME_FIRST);
                    if (_countDown <= 0) {
                        _game.resetDamage();
                        _stadium.openDoors();

                        _state = GameState.BATTLE_COUNTDOWN_SECOND;
                        _countDown = BATTLE_START_TIME_SECOND[0];
                        delay = getDelay(BATTLE_START_TIME_SECOND);
                    }

                    break;
                }
                // Battle start countdown, second part (10-0)
                case BATTLE_COUNTDOWN_SECOND: {
                    if (_countDown > 0) {
                        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_MATCH_WILL_START_IN_S1_SECOND_S);
                        sm.addInt(_countDown);
                        _stadium.broadcastPacket(sm);
                    }

                    delay = getDelay(BATTLE_START_TIME_SECOND);
                    if (_countDown <= 0) {
                        _state = GameState.BATTLE_STARTED;
                    }

                    break;
                }
                // Beginning of the battle
                case BATTLE_STARTED: {
                    _countDown = 0;
                    _state = GameState.BATTLE_IN_PROGRESS; // set state first, used in zone update
                    if (!startBattle()) {
                        _state = GameState.GAME_STOPPED;
                    }
                    break;
                }
                // Checks during battle
                case BATTLE_IN_PROGRESS: {
                    _countDown += 1000;
                    final int remaining = (int) ((Config.ALT_OLY_BATTLE - _countDown) / 1000);
                    for (int announceTime : BATTLE_END_TIME_SECOND) {
                        if (announceTime == remaining) {
                            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THE_GAME_WILL_END_IN_S1_SECOND_S);
                            sm.addInt(announceTime);
                            _stadium.broadcastPacket(sm);
                            break;
                        }
                    }

                    if (checkBattle() || (_countDown > Config.ALT_OLY_BATTLE)) {
                        _state = GameState.GAME_STOPPED;
                    }
                    break;
                }
                // End of the battle
                case GAME_STOPPED: {
                    _state = GameState.TELEPORT_TO_TOWN;
                    _countDown = TELEPORT_TO_TOWN_TIMES[0];
                    stopGame();
                    delay = getDelay(TELEPORT_TO_TOWN_TIMES);
                    break;
                }
                // Teleport to town countdown
                case TELEPORT_TO_TOWN: {
                    if (_countDown > 0) {
                        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_WILL_BE_MOVED_BACK_TO_TOWN_IN_S1_SECOND_S);
                        sm.addInt(_countDown);
                        _game.broadcastPacket(sm);
                    }

                    delay = getDelay(TELEPORT_TO_TOWN_TIMES);
                    if (_countDown <= 0) {
                        _state = GameState.CLEANUP;
                    }
                    break;
                }
                // Removals
                case CLEANUP: {
                    cleanupGame();
                    _state = GameState.IDLE;
                    _game = null;
                    return;
                }
            }
            ThreadPool.schedule(this, delay * 1000);
        } catch (Exception e) {
            switch (_state) {
                case GAME_STOPPED:
                case TELEPORT_TO_TOWN:
                case CLEANUP:
                case IDLE: {
                    LOGGER.warn("Unable to return players back in town, exception: " + e.getMessage());
                    _state = GameState.IDLE;
                    _game = null;
                    return;
                }
            }

            LOGGER.warn("Exception in " + _state + ", trying to port players back: " + e.getMessage(), e);
            _state = GameState.GAME_STOPPED;
            ThreadPool.schedule(this, 1000);
        }
    }

    private int getDelay(int[] times) {
        int time;
        for (int i = 0; i < (times.length - 1); i++) {
            time = times[i];
            if (time >= _countDown) {
                continue;
            }

            final int delay = _countDown - time;
            _countDown = time;
            return delay;
        }
        // should not happens
        _countDown = -1;
        return 1;
    }

    /**
     * Second stage: check for defaulted, port players to arena, announce game.
     *
     * @return true if no participants defaulted.
     */
    private boolean startGame() {
        try {
            // Checking for opponents and teleporting to arena
            if (_game.checkDefaulted()) {
                return false;
            }

            _stadium.closeDoors();
            if (_game.needBuffers()) {
                _stadium.spawnBuffers();
            }

            if (!_game.portPlayersToArena(_stadium.getZone().getSpawns(), _stadium.getInstance())) {
                return false;
            }

            _game.removals();
            _needAnnounce = true;
            OlympiadGameManager.getInstance().startBattle(); // inform manager
            return true;
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return false;
    }

    /**
     * Fourth stage: last checks, remove buffers, start competition itself.
     *
     * @return true if all participants online and ready on the stadium.
     */
    private boolean startBattle() {
        try {
            if (_game.needBuffers()) {
                _stadium.deleteBuffers();
            }

            if (_game.checkBattleStatus() && _game.makeCompetitionStart()) {
                // game successfully started
                _game.broadcastOlympiadInfo(_stadium);
                _stadium.broadcastPacket(SystemMessage.getSystemMessage(SystemMessageId.THE_MATCH_HAS_STARTED_FIGHT));
                _stadium.updateZoneStatusForCharactersInside();
                return true;
            }
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
        return false;
    }

    /**
     * Fifth stage: battle is running, returns true if winner found.
     *
     * @return
     */
    private boolean checkBattle() {
        try {
            return _game.haveWinner();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

        return true;
    }

    /**
     * Sixth stage: winner's validations
     */
    private void stopGame() {
        try {
            _game.validateWinner(_stadium);
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

        try {
            _game.cleanEffects();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

        try {
            _game.makePlayersInvul();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

        try {
            _stadium.updateZoneStatusForCharactersInside();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    /**
     * Seventh stage: game cleanup (port players back, closing doors, etc)
     */
    private void cleanupGame() {
        try {
            _game.removePlayersInvul();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

        try {
            _game.playersStatusBack();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

        try {
            _game.portPlayersBack();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

        try {
            _game.clearPlayers();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }

        try {
            _stadium.closeDoors();
        } catch (Exception e) {
            LOGGER.warn(e.getMessage(), e);
        }
    }

    private enum GameState {
        BEGIN,
        TELEPORT_TO_ARENA,
        GAME_STARTED,
        BATTLE_COUNTDOWN_FIRST,
        BATTLE_COUNTDOWN_SECOND,
        BATTLE_STARTED,
        BATTLE_IN_PROGRESS,
        GAME_STOPPED,
        TELEPORT_TO_TOWN,
        CLEANUP,
        IDLE
    }
}