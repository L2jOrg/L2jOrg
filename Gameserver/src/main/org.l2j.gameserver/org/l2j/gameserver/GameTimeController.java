package org.l2j.gameserver;

import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.OnDayNightChange;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Game Time controller class.
 *
 * @author Forsaiken
 */
public final class GameTimeController extends Thread {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(GameTimeController.class);

    public static final int TICKS_PER_SECOND = 10; // not able to change this without checking through code
    public static final int MILLIS_IN_TICK = 1000 / TICKS_PER_SECOND;
    public static final int IN_GAME_DAYS_PER_DAY = 6;
    public static final int MILLIS_PER_IN_GAME_DAY = (3600000 * 24) / IN_GAME_DAYS_PER_DAY;
    public static final int SECONDS_PER_IN_GAME_DAY = MILLIS_PER_IN_GAME_DAY / 1000;
    public static final int TICKS_PER_IN_GAME_DAY = SECONDS_PER_IN_GAME_DAY * TICKS_PER_SECOND;

    private final Set<L2Character> _movingObjects = ConcurrentHashMap.newKeySet();
    private final Set<L2Character> _shadowSenseCharacters = ConcurrentHashMap.newKeySet();
    private final long _referenceTime;
    private volatile boolean shutdown = false;

    private GameTimeController() {
        super("GameTimeController");
        super.setDaemon(true);
        super.setPriority(MAX_PRIORITY);

        final Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        _referenceTime = c.getTimeInMillis();
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
        return (int) ((System.currentTimeMillis() - _referenceTime) / MILLIS_IN_TICK);
    }

    /**
     * Add a L2Character to movingObjects of GameTimeController.
     *
     * @param cha The L2Character to add to movingObjects of GameTimeController
     */
    public final void registerMovingObject(L2Character cha) {
        if (cha == null) {
            return;
        }

        _movingObjects.add(cha);
    }

    /**
     * Move all L2Characters contained in movingObjects of GameTimeController.<BR>
     * <B><U> Concept</U> :</B><BR>
     * All L2Character in movement are identified in <B>movingObjects</B> of GameTimeController.<BR>
     * <B><U> Actions</U> :</B><BR>
     * <ul>
     * <li>Update the position of each L2Character</li>
     * <li>If movement is finished, the L2Character is removed from movingObjects</li>
     * <li>Create a task to update the _knownObject and _knowPlayers of each L2Character that finished its movement and of their already known L2Object then notify AI with EVT_ARRIVED</li>
     * </ul>
     */
    private void moveObjects() {
        _movingObjects.removeIf(L2Character::updatePosition);
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
            nextTickTime = ((System.currentTimeMillis() / MILLIS_IN_TICK) * MILLIS_IN_TICK) + 100;

            try {
                moveObjects();
            } catch (Throwable e) {
                LOGGER.warn(e.getLocalizedMessage(), e);
            }

            sleepTime = nextTickTime - System.currentTimeMillis();
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

    public synchronized void addShadowSenseCharacter(L2Character character) {
        if (!_shadowSenseCharacters.contains(character)) {
            _shadowSenseCharacters.add(character);
            if (isNight()) {
                final SystemMessage msg = SystemMessage.getSystemMessage(SystemMessageId.IT_IS_NOW_MIDNIGHT_AND_THE_EFFECT_OF_S1_CAN_BE_FELT);
                msg.addSkillName(CommonSkill.SHADOW_SENSE_ID.getId());
                character.sendPacket(msg);
            }
        }
    }

    public void removeShadowSenseCharacter(L2Character character) {
        _shadowSenseCharacters.remove(character);
    }

    private void notifyShadowSense() {
        final SystemMessage msg = SystemMessage.getSystemMessage(isNight() ? SystemMessageId.IT_IS_NOW_MIDNIGHT_AND_THE_EFFECT_OF_S1_CAN_BE_FELT : SystemMessageId.IT_IS_DAWN_AND_THE_EFFECT_OF_S1_WILL_NOW_DISAPPEAR);
        msg.addSkillName(CommonSkill.SHADOW_SENSE_ID.getId());
        for (L2Character character : _shadowSenseCharacters) {
            character.getStat().recalculateStats(true);
            character.sendPacket(msg);
        }
    }

    public static GameTimeController getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final GameTimeController INSTANCE = new GameTimeController();
    }
}