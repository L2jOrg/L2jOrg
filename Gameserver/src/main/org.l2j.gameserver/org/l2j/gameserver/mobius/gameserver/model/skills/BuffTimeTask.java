/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.mobius.gameserver.model.skills;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Effect time task finish the effect when the abnormal time is reached.
 *
 * @author Zoey76
 */
public class BuffTimeTask implements Runnable {
    private final AtomicInteger _time = new AtomicInteger();
    private final BuffInfo _info;

    /**
     * EffectTimeTask constructor.
     *
     * @param info the buff info
     */
    public BuffTimeTask(BuffInfo info) {
        _info = info;
    }

    /**
     * Gets the elapsed time.
     *
     * @return the tick count
     */
    public int getElapsedTime() {
        return _time.get();
    }

    @Override
    public void run() {
        if ((_info.getEffected() != null) && (_time.incrementAndGet() > _info.getAbnormalTime())) {
            _info.getEffected().getEffectList().stopSkillEffects(false, _info.getSkill().getId());
        }
    }
}
