package l2s.gameserver.data.xml.holder;

import gnu.trove.map.hash.TIntObjectHashMap;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.templates.ShuttleTemplate;

/**
 * @author Bonux
 */
public final class ShuttleTemplateHolder extends AbstractHolder
{
	private static final ShuttleTemplateHolder _instance = new ShuttleTemplateHolder();

	private TIntObjectHashMap<ShuttleTemplate> _templates = new TIntObjectHashMap<ShuttleTemplate>();

	public static ShuttleTemplateHolder getInstance()
	{
		return _instance;
	}

	public void addTemplate(ShuttleTemplate template)
	{
		_templates.put(template.getId(), template);
	}

	public ShuttleTemplate getTemplate(int id)
	{
		return _templates.get(id);
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