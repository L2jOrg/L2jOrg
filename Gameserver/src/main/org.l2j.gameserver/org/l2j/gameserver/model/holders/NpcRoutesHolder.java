package org.l2j.gameserver.model.holders;

import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.interfaces.ILocational;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds depending between NPCs spawn point and route
 *
 * @author GKR
 */
public final class NpcRoutesHolder {
    private final Map<String, String> _correspondences;

    public NpcRoutesHolder() {
        _correspondences = new HashMap<>();
    }

    /**
     * Add correspondence between specific route and specific spawn point
     *
     * @param routeName name of route
     * @param loc       Location of spawn point
     */
    public void addRoute(String routeName, Location loc) {
        _correspondences.put(getUniqueKey(loc), routeName);
    }

    /**
     * @param npc
     * @return route name for given NPC.
     */
    public String getRouteName(Npc npc) {
        if (npc.getSpawn() != null) {
            final String key = getUniqueKey(npc.getSpawn().getLocation());
            return _correspondences.getOrDefault(key, "");
        }
        return "";
    }

    /**
     * @param loc
     * @return unique text string for given Location.
     */
    private String getUniqueKey(ILocational loc) {
        return (loc.getX() + "-" + loc.getY() + "-" + loc.getZ());
    }
}
