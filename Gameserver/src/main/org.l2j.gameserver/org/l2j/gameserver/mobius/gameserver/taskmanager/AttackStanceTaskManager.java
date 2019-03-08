package org.l2j.gameserver.mobius.gameserver.taskmanager;

import org.l2j.gameserver.ThreadPoolManager;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.AutoAttackStop;

import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Attack stance task manager.
 * @author Luca Baldi, Zoey76
 */
public class AttackStanceTaskManager
{
    protected static final Logger LOGGER = Logger.getLogger(AttackStanceTaskManager.class.getName());

    protected static final Map<L2Character, Long> _attackStanceTasks = new ConcurrentHashMap<>();

    public static final long COMBAT_TIME = 15_000;

    /**
     * Instantiates a new attack stance task manager.
     */
    protected AttackStanceTaskManager()
    {
        ThreadPoolManager.getInstance().scheduleAtFixedRate(new FightModeScheduler(), 0, 1000);
    }

    /**
     * Adds the attack stance task.
     * @param actor the actor
     */
    public void addAttackStanceTask(L2Character actor)
    {
        if (actor != null)
        {
            _attackStanceTasks.put(actor, System.currentTimeMillis());
        }
    }

    /**
     * Removes the attack stance task.
     * @param actor the actor
     */
    public void removeAttackStanceTask(L2Character actor)
    {
        if (actor != null)
        {
            if (actor.isSummon())
            {
                actor = actor.getActingPlayer();
            }
            _attackStanceTasks.remove(actor);
        }
    }

    /**
     * Checks for attack stance task.<br>
     * @param actor the actor
     * @return {@code true} if the character has an attack stance task, {@code false} otherwise
     */
    public boolean hasAttackStanceTask(L2Character actor)
    {
        if (actor != null)
        {
            if (actor.isSummon())
            {
                actor = actor.getActingPlayer();
            }
            return _attackStanceTasks.containsKey(actor);
        }
        return false;
    }

    protected class FightModeScheduler implements Runnable
    {
        @Override
        public void run()
        {
            final long current = System.currentTimeMillis();
            try
            {
                final Iterator<Entry<L2Character, Long>> iter = _attackStanceTasks.entrySet().iterator();
                Entry<L2Character, Long> e;
                L2Character actor;
                while (iter.hasNext())
                {
                    e = iter.next();
                    if ((current - e.getValue()) > COMBAT_TIME)
                    {
                        actor = e.getKey();
                        if (actor != null)
                        {
                            actor.broadcastPacket(new AutoAttackStop(actor.getObjectId()));
                            actor.getAI().setAutoAttacking(false);
                            if (actor.isPlayer() && actor.hasSummon())
                            {
                                final L2Summon pet = actor.getPet();
                                if (pet != null)
                                {
                                    pet.broadcastPacket(new AutoAttackStop(pet.getObjectId()));
                                }
                                actor.getServitors().values().forEach(s -> s.broadcastPacket(new AutoAttackStop(s.getObjectId())));
                            }
                        }
                        iter.remove();
                    }
                }
            }
            catch (Exception e)
            {
                // Unless caught here, players remain in attack positions.
                LOGGER.log(Level.WARNING, "Error in FightModeScheduler: " + e.getMessage(), e);
            }
        }
    }

    /**
     * Gets the single instance of AttackStanceTaskManager.
     * @return single instance of AttackStanceTaskManager
     */
    public static AttackStanceTaskManager getInstance()
    {
        return SingletonHolder._instance;
    }

    private static class SingletonHolder
    {
        protected static final AttackStanceTaskManager _instance = new AttackStanceTaskManager();
    }
}
