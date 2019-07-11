package org.l2j.gameserver.model.interfaces;

import org.l2j.gameserver.model.zone.type.BannedSpawnTerritory;
import org.l2j.gameserver.model.zone.type.L2SpawnTerritory;

import java.util.List;

/**
 * @author UnAfraid
 */
public interface ITerritorized {
    void addTerritory(L2SpawnTerritory territory);

    List<L2SpawnTerritory> getTerritories();

    void addBannedTerritory(BannedSpawnTerritory territory);

    List<BannedSpawnTerritory> getBannedTerritories();
}
