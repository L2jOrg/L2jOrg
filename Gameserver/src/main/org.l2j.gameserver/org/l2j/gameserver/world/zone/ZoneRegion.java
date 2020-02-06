package org.l2j.gameserver.world.zone;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.Skill;
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
