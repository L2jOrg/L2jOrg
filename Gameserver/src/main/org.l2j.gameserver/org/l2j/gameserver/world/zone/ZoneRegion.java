package org.l2j.gameserver.world.zone;

import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.world.zone.type.PeaceZone;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Nos
 */
public class ZoneRegion {
    private final int _regionX;
    private final int _regionY;
    private final Map<Integer, Zone> _zones = new ConcurrentHashMap<>();

    public ZoneRegion(int regionX, int regionY) {
        _regionX = regionX;
        _regionY = regionY;
    }

    public Map<Integer, Zone> getZones() {
        return _zones;
    }

    public int getRegionX() {
        return _regionX;
    }

    public int getRegionY() {
        return _regionY;
    }

    public void revalidateZones(Creature character) {
        // do NOT update the world region while the character is still in the process of teleporting
        // Once the teleport is COMPLETED, revalidation occurs safely, at that time.

        if (character.isTeleporting()) {
            return;
        }

        for (Zone z : _zones.values()) {
            z.revalidateInZone(character);
        }
    }

    public void removeFromZones(Creature character) {
        for (Zone z : _zones.values()) {
            z.removeCharacter(character);
        }
    }

    public boolean checkEffectRangeInsidePeaceZone(Skill skill, int x, int y, int z) {
        final int range = skill.getEffectRange();
        final int up = y + range;
        final int down = y - range;
        final int left = x + range;
        final int right = x - range;

        for (Zone e : _zones.values()) {
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

    public void onDeath(Creature character) {
        for (Zone z : _zones.values()) {
            if (z.isInsideZone(character)) {
                z.onDieInside(character);
            }
        }
    }

    public void onRevive(Creature character) {
        for (Zone z : _zones.values()) {
            if (z.isInsideZone(character)) {
                z.onReviveInside(character);
            }
        }
    }
}
