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
package org.l2j.gameserver.model;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.ControllableMob;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

/**
 * @author littlecrow A special spawn implementation to spawn controllable mob
 */
public class GroupSpawn extends Spawn {
    private final NpcTemplate _template;

    public GroupSpawn(NpcTemplate mobTemplate) throws SecurityException, ClassNotFoundException, NoSuchMethodException {
        super(mobTemplate);
        _template = mobTemplate;

        setAmount(1);
    }

    public Npc doGroupSpawn() {
        try {
            if (_template.isType("Pet") || _template.isType("Minion")) {
                return null;
            }

            int newlocx = 0;
            int newlocy = 0;
            int newlocz = 0;

            if ((getX() == 0) && (getY() == 0)) {
                if (getLocationId() == 0) {
                    return null;
                }

                return null;
            }

            newlocx = getX();
            newlocy = getY();
            newlocz = getZ();

            final Npc mob = new ControllableMob(_template);
            mob.setCurrentHpMp(mob.getMaxHp(), mob.getMaxMp());

            mob.setHeading(getHeading() == -1 ? Rnd.get(61794) : getHeading());

            mob.setSpawn(this);
            mob.spawnMe(newlocx, newlocy, newlocz);
            return mob;
        } catch (Exception e) {
            LOGGER.warn("NPC class not found: " + e.getMessage(), e);
            return null;
        }
    }
}