package org.l2j.gameserver.mobius.gameserver.model.zone;

import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.skills.Skill;
import org.l2j.gameserver.mobius.gameserver.model.zone.type.L2PeaceZone;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Nos
 */
public class ZoneRegion {
    private final int _regionX;
    private final int _regionY;
    private final Map<Integer, L2ZoneType> _zones = new ConcurrentHashMap<>();

    public ZoneRegion(int regionX, int regionY) {
        _regionX = regionX;
        _regionY = regionY;
    }

    public Map<Integer, L2ZoneType> getZones() {
        return _zones;
    }

    public int getRegionX() {
        return _regionX;
    }

    public int getRegionY() {
        return _regionY;
    }

    public void revalidateZones(L2Character character) {
        // do NOT update the world region while the character is still in the process of teleporting
        // Once the teleport is COMPLETED, revalidation occurs safely, at that time.

        if (character.isTeleporting()) {
            return;
        }

        for (L2ZoneType z : _zones.values()) {
            z.revalidateInZone(character);
        }
    }

    public void removeFromZones(L2Character character) {
        for (L2ZoneType z : _zones.values()) {
            z.removeCharacter(character);
        }
    }

    public boolean checkEffectRangeInsidePeaceZone(Skill skill, int x, int y, int z) {
        final int range = skill.getEffectRange();
        final int up = y + range;
        final int down = y - range;
        final int left = x + range;
        final int right = x - range;

        for (L2ZoneType e : _zones.values()) {
            if (e instanceof L2PeaceZone) {
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

    public void onDeath(L2Character character) {
        for (L2ZoneType z : _zones.values()) {
            if (z.isInsideZone(character)) {
                z.onDieInside(character);
            }
        }
    }

    public void onRevive(L2Character character) {
        for (L2ZoneType z : _zones.values()) {
            if (z.isInsideZone(character)) {
                z.onReviveInside(character);
            }
        }
    }
}
