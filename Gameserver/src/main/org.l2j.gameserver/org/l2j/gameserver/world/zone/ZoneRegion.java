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
package org.l2j.gameserver.world.zone;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.world.zone.type.PeaceZone;

/**
 * @author Nos
 */
public class ZoneRegion {

    private final IntMap<Zone> zones = new CHashIntMap<>();

    public IntMap<Zone> getZones() {
        return zones;
    }

    public void revalidateZones(Creature creature) {
        // do NOT update the world region while the character is still in the process of teleporting
        // Once the teleport is COMPLETED, revalidation occurs safely, at that time.

        if (creature.isTeleporting()) {
            return;
        }

        zones.values().forEach(z -> z.revalidateInZone(creature));
    }

    public void removeFromZones(Creature creature) {
        zones.values().forEach(z -> z.removeCreature(creature));
    }

    public boolean checkEffectRangeInsidePeaceZone(Skill skill, int x, int y, int z) {
        final int range = skill.getEffectRange();
        final int up = y + range;
        final int down = y - range;
        final int left = x + range;
        final int right = x - range;

        for (Zone e : zones.values()) {
            if (e instanceof PeaceZone) {
                if (e.isInsideZone(x, up, z)) {
                    return false;
                }

                if (e.isInsideZone(x, down, z)) {
                    return false;
                }

                if (e.isInsideZone(left, y, z)) {
                    return false;
                }

                if (e.isInsideZone(right, y, z)) {
                    return false;
                }

                if (e.isInsideZone(x, y, z)) {
                    return false;
                }
            }
        }
        return true;
    }

    public void onDeath(Creature creature) {
        zones.values().stream().filter(z -> z.isInsideZone(creature)).forEach(z -> z.onDieInside(creature));
    }

    public void onRevive(Creature creature) {
        zones.values().stream().filter(z -> z.isInsideZone(creature)).forEach(z -> z.onReviveInside(creature));
    }
}
