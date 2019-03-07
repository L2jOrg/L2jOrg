package org.l2j.gameserver.mobius.gameserver.model.interfaces;

import org.l2j.gameserver.mobius.gameserver.model.zone.type.L2BannedSpawnTerritory;
import org.l2j.gameserver.mobius.gameserver.model.zone.type.L2SpawnTerritory;

import java.util.List;

/**
 * @author UnAfraid
 */
public interface ITerritorized
{
    void addTerritory(L2SpawnTerritory territory);

    List<L2SpawnTerritory> getTerritories();

    void addBannedTerritory(L2BannedSpawnTerritory territory);

    List<L2BannedSpawnTerritory> getBannedTerritories();
}
