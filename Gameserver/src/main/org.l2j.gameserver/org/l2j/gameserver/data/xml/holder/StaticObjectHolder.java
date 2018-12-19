package org.l2j.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.model.instances.StaticObjectInstance;
import org.l2j.gameserver.templates.StaticObjectTemplate;

/**
 * @author VISTALL
 * @date 22:21/09.03.2011
 */
public final class StaticObjectHolder extends AbstractHolder
{
	private static final StaticObjectHolder _instance = new StaticObjectHolder();

	private TIntObjectMap<StaticObjectTemplate> _templates = new TIntObjectHashMap<>();
	private TIntObjectMap<StaticObjectInstance> _spawned = new TIntObjectHashMap<>();

	public static StaticObjectHolder getInstance()
	{
		return _instance;
	}

	public void addTemplate(StaticObjectTemplate template)
	{
		_templates.put(template.getUId(), template);
	}

	public StaticObjectTemplate getTemplate(int id)
	{
		return _templates.get(id);
	}

	public void spawnAll()
	{
		for(StaticObjectTemplate template : _templates.valueCollection())
			if(template.isSpawn())
			{
				StaticObjectInstance obj = template.newInstance();

				_spawned.put(template.getUId(), obj);
			}
		logger.info("spawned: " + _spawned.size() + " static object(s).");
	}

	public StaticObjectInstance getObject(int id)
	{
		return _spawned.get(id);
	}

	@Override
	public int size()
	{
		return _templates.size();
	}

	@Override
	public void clear()
	{
		_templates.clear();
	}
}