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
package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.data.xml.impl.NpcData;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.templates.L2NpcTemplate;

/**
 * This class manages all chest.
 *
 * @author Julian
 */
public final class Chest extends Monster {
    private volatile boolean _specialDrop;

    public Chest(L2NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2ChestInstance);
        setRandomWalking(false);
        _specialDrop = false;
    }

    @Override
    public void onSpawn() {
        super.onSpawn();
        _specialDrop = false;
        setMustRewardExpSp(true);
    }

    public synchronized void setSpecialDrop() {
        _specialDrop = true;
    }

    @Override
    public void doItemDrop(L2NpcTemplate npcTemplate, Creature lastAttacker) {
        int id = getTemplate().getId();

        if (!_specialDrop) {
            if ((id >= 18265) && (id <= 18286)) {
                id += 3536;
            } else if ((id == 18287) || (id == 18288)) {
                id = 21671;
            } else if ((id == 18289) || (id == 18290)) {
                id = 21694;
            } else if ((id == 18291) || (id == 18292)) {
                id = 21717;
            } else if ((id == 18293) || (id == 18294)) {
                id = 21740;
            } else if ((id == 18295) || (id == 18296)) {
                id = 21763;
            } else if ((id == 18297) || (id == 18298)) {
                id = 21786;
            }
        }
        super.doItemDrop(NpcData.getInstance().getTemplate(id), lastAttacker);
    }

    @Override
    public boolean isMovementDisabled() {
        return true;
    }

    @Override
    public boolean hasRandomAnimation() {
        return false;
    }
}
