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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.ai.ControllableMobAI;
import org.l2j.gameserver.ai.CreatureAI;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

/**
 * @author littlecrow
 */
public class ControllableMob extends Monster {
    private boolean _isInvul;

    public ControllableMob(NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2ControllableMobInstance);
    }

    @Override
    public boolean isAggressive() {
        return true;
    }

    @Override
    public int getAggroRange() {
        // force mobs to be aggro
        return 500;
    }

    @Override
    protected CreatureAI initAI() {
        return new ControllableMobAI(this);
    }

    @Override
    public void detachAI() {
        // do nothing, AI of controllable mobs can't be detached automatically
    }

    @Override
    public boolean isInvul() {
        return _isInvul;
    }

    public void setInvul(boolean isInvul) {
        _isInvul = isInvul;
    }

    @Override
    public boolean doDie(Creature killer) {
        if (!super.doDie(killer)) {
            return false;
        }

        setAI(null);
        return true;
    }
}