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
package org.l2j.gameserver.taskmanager;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Npc;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import static org.l2j.gameserver.util.GameUtils.isAttackable;

/**
 * @author Mobius
 */
public class RandomAnimationTaskManager
{
    private static final Map<Npc, Long> PENDING_ANIMATIONS = new ConcurrentHashMap<>();

    public RandomAnimationTaskManager()
    {
        ThreadPool.scheduleAtFixedRate(() ->
        {
            final long time = System.currentTimeMillis();
            for (Entry<Npc, Long> entry : PENDING_ANIMATIONS.entrySet())
            {
                if (time > entry.getValue())
                {
                    final Npc npc = entry.getKey();
					if (npc.isInActiveRegion() && !npc.isDead() && !npc.isInCombat() && !npc.isMoving() && !npc.hasBlockActions())
                    {
                        npc.onRandomAnimation(Rnd.get(2, 3));
                    }

                    PENDING_ANIMATIONS.put(npc, time + (Rnd.get((isAttackable(npc) ? Config.MIN_MONSTER_ANIMATION : Config.MIN_NPC_ANIMATION), (isAttackable(npc) ? Config.MAX_MONSTER_ANIMATION : Config.MAX_NPC_ANIMATION)) * 1000));
                }
            }
        }, 0, 1000);
    }

    public void add(Npc npc)
    {
        if (npc.hasRandomAnimation())
        {
            PENDING_ANIMATIONS.putIfAbsent(npc, System.currentTimeMillis() + (Rnd.get((isAttackable(npc) ? Config.MIN_MONSTER_ANIMATION : Config.MIN_NPC_ANIMATION), (isAttackable(npc) ? Config.MAX_MONSTER_ANIMATION : Config.MAX_NPC_ANIMATION)) * 1000));
        }
    }

    public void remove(Npc npc)
    {
        PENDING_ANIMATIONS.remove(npc);
    }

    public static RandomAnimationTaskManager getInstance()
    {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder
    {
        protected static final RandomAnimationTaskManager INSTANCE = new RandomAnimationTaskManager();
    }
}
