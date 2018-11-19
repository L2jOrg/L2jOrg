package l2s.gameserver.data.xml.holder;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.model.instances.StaticObjectInstance;
import l2s.gameserver.templates.StaticObjectTemplate;
import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

/**
 * @author VISTALL
 * @date 22:21/09.03.2011
 */
public final class StaticObjectHolder extends AbstractHolder
{
	private static final StaticObjectHolder _instance = new StaticObjectHolder();

	private IntObjectMap<StaticObjectTemplate> _templates = new HashIntObjectMap<StaticObjectTemplate>();
	private IntObjectMap<StaticObjectInstance> _spawned = new HashIntObjectMap<StaticObjectInstance>();

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
		for(StaticObjectTemplate template : _templates.values())
			if(template.isSpawn())
			{
				StaticObjectInstance obj = template.newInstance();

				_spawned.put(template.getUId(), obj);
			}
		info("spawned: " + _spawned.size() + " static object(s).");
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