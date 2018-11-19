package l2s.gameserver.data.xml.holder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.templates.spawn.SpawnTemplate;

/**
 * @author VISTALL
 * @date  18:38/10.12.2010
 */
public final class SpawnHolder extends AbstractHolder
{
	private static final SpawnHolder _instance = new SpawnHolder();

	private Map<String, List<SpawnTemplate>> _spawns = new HashMap<String, List<SpawnTemplate>>();

	public static SpawnHolder getInstance()
	{
		return _instance;
	}

	public void addSpawn(String group, SpawnTemplate spawn)
	{
		List<SpawnTemplate> spawns = _spawns.get(group);
		if(spawns == null)
			_spawns.put(group, (spawns = new ArrayList<SpawnTemplate>()));
		spawns.add(spawn);
	}

	public List<SpawnTemplate> getSpawn(String name)
	{
		List<SpawnTemplate> template = _spawns.get(name);
		return template == null ? Collections.<SpawnTemplate>emptyList() : template;
	}

	@Override
	public int size()
	{
		int i = 0;
		for(List<SpawnTemplate> l : _spawns.values())
			i += l.size();

		return i;
	}

	@Override
	public void clear()
	{
		_spawns.clear();
	}

	public Map<String, List<SpawnTemplate>> getSpawns()
	{
		return _spawns;
	}
}