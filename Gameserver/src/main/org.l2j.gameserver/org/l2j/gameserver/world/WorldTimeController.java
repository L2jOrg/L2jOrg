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
package org.l2j.gameserver.world;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.OnDayNightChange;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static java.lang.System.currentTimeMillis;

/**
 * World Time controller class.
 *
 * @author Forsaiken
 */
public final class WorldTimeController extends Thread {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(WorldTimeController.class);

    public static final int TICKS_PER_SECOND = 10; // not able to change this without checking through code
    public static final int MILLIS_IN_TICK = 1000 / TICKS_PER_SECOND;
    private static final int IN_GAME_DAYS_PER_DAY = 6;
    private static final int MILLIS_PER_IN_GAME_DAY = (3600000 * 24) / IN_GAME_DAYS_PER_DAY;
    private static final int SECONDS_PER_IN_GAME_DAY = MILLIS_PER_IN_GAME_DAY / 1000;
    private static final int TICKS_PER_IN_GAME_DAY = SECONDS_PER_IN_GAME_DAY * TICKS_PER_SECOND;

    private final Set<Creature> movingObjects = ConcurrentHashMap.newKeySet();
    private final Set<Creature> shadowSenseCharacters = ConcurrentHashMap.newKeySet();

    private final long referenceTime;
    private volatile boolean shutdown = false;

    private WorldTimeController() {
        super("World Time Controller");
        setDaemon(true);
        setPriority(MAX_PRIORITY);

        referenceTime = LocalDate.now().atStartOfDay(ZoneId.systemDefault()).toInstant().toEpochMilli();
    }

    public static void init() {
        getInstance().start();
    }

    public final int getGameTime() {
        return (getGameTicks() % TICKS_PER_IN_GAME_DAY) / MILLIS_IN_TICK;
    }

    public final int getGameHour() {
        return getGameTime() / 60;
    }

    public final int getGameMinute() {
        return getGameTime() % 60;
    }

    public final boolean isNight() {
        return getGameHour() < 6;
    }

    /**
     * The true GameTime tick. Directly taken from current time. This represents the tick of the time.
     *
     * @return
     */
    public final int getGameTicks() {
        return (int) ((currentTimeMillis() - referenceTime) / MILLIS_IN_TICK);
    }

    /**
     * Add a Creature to movingObjects of GameTimeController.
     *
     * @param cha The Creature to add to movingObjects of GameTimeController
     */
    public final void registerMovingObject(Creature cha) {
        if (cha == null) {
            return;
        }

        movingObjects.add(cha);
    }

    /**
     * Move all L2Characters contained in movingObjects of GameTimeController.<BR>
     * <B><U> Concept</U> :</B><BR>
     * All Creature in movement are identified in <B>movingObjects</B> of GameTimeController.<BR>
     * <B><U> Actions</U> :</B><BR>
     * <ul>
     * <li>Update the position of each Creature</li>
     * <li>If movement is finished, the Creature is removed from movingObjects</li>
     * <li>Create a task to update the _knownObject and _knowPlayers of each Creature that finished its movement and of their already known WorldObject then notify AI with EVT_ARRIVED</li>
     * </ul>
     */
    private void moveObjects() {
        movingObjects.removeIf(Creature::updatePosition);
    }

    public final void stopTimer() {
        shutdown = true;
    }

    @Override
    public final void run() {
        long nextTickTime;
        long sleepTime;
        boolean isNight = isNight();

        EventDispatcher.getInstance().notifyEventAsync(OnDayNightChange.of(isNight));

        while (!shutdown) {
            nextTickTime = ((currentTimeMillis() / MILLIS_IN_TICK) * MILLIS_IN_TICK) + 100;

            try {
                moveObjects();
            } catch (Throwable e) {
                LOGGER.warn(e.getLocalizedMessage(), e);
            }

            sleepTime = nextTickTime - currentTimeMillis();
            if (sleepTime > 0) {
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    // ignore
                }
            }

            if (isNight() != isNight) {
                isNight = !isNight;
                EventDispatcher.getInstance().notifyEventAsync(OnDayNightChange.of(isNight));
                notifyShadowSense();
            }
        }
    }

    public synchronized void addShadowSenseCharacter(Creature character) {
        if (!shadowSenseCharacters.contains(character)) {
            shadowSenseCharacters.add(character);
            if (isNight()) {
                final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.IT_IS_NOW_MIDNIGHT_AND_THE_EFFECT_OF_S1_CAN_BE_FELT);
                msg.addSkillName(CommonSkill.SHADOW_SENSE_ID.getId());
                character.sendPacket(msg);
            }
        }
    }

    public void removeShadowSenseCharacter(Creature character) {
        shadowSenseCharacters.remove(character);
    }

    private void notifyShadowSense() {
        final SystemMessage msg = SystemMessage.getSystemMessage(isNight() ? SystemMessageId.IT_IS_NOW_MIDNIGHT_AND_THE_EFFECT_OF_S1_CAN_BE_FELT : SystemMessageId.IT_IS_DAWN_AND_THE_EFFECT_OF_S1_WILL_NOW_DISAPPEAR);
        msg.addSkillName(CommonSkill.SHADOW_SENSE_ID.getId());
        for (Creature character : shadowSenseCharacters) {
            character.getStats().recalculateStats(true);
            character.sendPacket(msg);
        }
    }

    public static WorldTimeController getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final WorldTimeController INSTANCE = new WorldTimeController();
    }
}