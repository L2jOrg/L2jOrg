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
import org.l2j.gameserver.ai.CreatureAI;
import org.l2j.gameserver.model.actor.Attackable;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;



/**
 * @author Mobius
 */
public class AttackableThinkTaskManager
{
    private static final Set<Attackable> ATTACKABLES = ConcurrentHashMap.newKeySet();
    private static boolean _working = false;

    public AttackableThinkTaskManager()
    {
        ThreadPool.scheduleAtFixedRate(() ->
        {
            if (_working)
            {
                return;
            }
            _working = true;

            CreatureAI ai;
            for (Attackable attackable : ATTACKABLES)
            {
                if (attackable.hasAI())
                {
                    ai = attackable.getAI();
                    if (ai != null)
                    {
                        ai.onEvtThink();
                    }
                    else
                    {
                        remove(attackable);
                    }
                }
                else
                {
                    remove(attackable);
                }
            }

            _working = false;
        }, 1000, 1000);
    }

    public void add(Attackable attackable)
    {
        if (!ATTACKABLES.contains(attackable))
        {
            ATTACKABLES.add(attackable);
        }
    }

    public void remove(Attackable attackable)
    {
        ATTACKABLES.remove(attackable);
    }

    public static AttackableThinkTaskManager getInstance()
    {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder
    {
        protected static final AttackableThinkTaskManager INSTANCE = new AttackableThinkTaskManager();
    }
}
