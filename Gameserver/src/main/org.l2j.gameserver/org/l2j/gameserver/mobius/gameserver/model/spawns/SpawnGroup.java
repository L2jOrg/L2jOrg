package org.l2j.gameserver.mobius.gameserver.model.spawns;

import org.l2j.gameserver.mobius.gameserver.model.StatsSet;
import org.l2j.gameserver.mobius.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.mobius.gameserver.model.interfaces.IParameterized;
import org.l2j.gameserver.mobius.gameserver.model.interfaces.ITerritorized;
import org.l2j.gameserver.mobius.gameserver.model.zone.type.L2BannedSpawnTerritory;
import org.l2j.gameserver.mobius.gameserver.model.zone.type.L2SpawnTerritory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author UnAfraid
 */
public class SpawnGroup implements Cloneable, ITerritorized, IParameterized<StatsSet>
{
	private final String _name;
	private final boolean _spawnByDefault;
	private List<L2SpawnTerritory> _territories;
	private List<L2BannedSpawnTerritory> _bannedTerritories;
	private final List<NpcSpawnTemplate> _spawns = new ArrayList<>();
	private StatsSet _parameters;
	
	public SpawnGroup(StatsSet set)
	{
		this(set.getString("name", null), set.getBoolean("spawnByDefault", true));
	}
	
	private SpawnGroup(String name, boolean spawnByDefault)
	{
		_name = name;
		_spawnByDefault = spawnByDefault;
	}
	
	public String getName()
	{
		return _name;
	}
	
	public boolean isSpawningByDefault()
	{
		return _spawnByDefault;
	}
	
	public void addSpawn(NpcSpawnTemplate template)
	{
		_spawns.add(template);
	}
	
	public List<NpcSpawnTemplate> getSpawns()
	{
		return _spawns;
	}
	
	@Override
	public void addTerritory(L2SpawnTerritory territory)
	{
		if (_territories == null)
		{
			_territories = new ArrayList<>();
		}
		_territories.add(territory);
	}
	
	@Override
	public List<L2SpawnTerritory> getTerritories()
	{
		return _territories != null ? _territories : Collections.emptyList();
	}
	
	@Override
	public void addBannedTerritory(L2BannedSpawnTerritory territory)
	{
		if (_bannedTerritories == null)
		{
			_bannedTerritories = new ArrayList<>();
		}
		_bannedTerritories.add(territory);
	}
	
	@Override
	public List<L2BannedSpawnTerritory> getBannedTerritories()
	{
		return _bannedTerritories != null ? _bannedTerritories : Collections.emptyList();
	}
	
	@Override
	public StatsSet getParameters()
	{
		return _parameters;
	}
	
	@Override
	public void setParameters(StatsSet parameters)
	{
		_parameters = parameters;
	}
	
	public List<NpcSpawnTemplate> getSpawnsById(int id)
	{
		return _spawns.stream().filter(spawn -> spawn.getId() == id).collect(Collectors.toList());
	}
	
	public void spawnAll()
	{
		spawnAll(null);
	}
	
	public void spawnAll(Instance instance)
	{
		_spawns.forEach(template -> template.spawn(instance));
	}
	
	public void despawnAll()
	{
		_spawns.forEach(NpcSpawnTemplate::despawn);
	}
	
	@Override
	public SpawnGroup clone()
	{
		final SpawnGroup group = new SpawnGroup(_name, _spawnByDefault);
		
		// Clone banned territories
		for (L2BannedSpawnTerritory territory : getBannedTerritories())
		{
			group.addBannedTerritory(territory);
		}
		
		// Clone territories
		for (L2SpawnTerritory territory : getTerritories())
		{
			group.addTerritory(territory);
		}
		
		// Clone spawns
		for (NpcSpawnTemplate spawn : _spawns)
		{
			group.addSpawn(spawn.clone());
		}
		
		return group;
	}
}
