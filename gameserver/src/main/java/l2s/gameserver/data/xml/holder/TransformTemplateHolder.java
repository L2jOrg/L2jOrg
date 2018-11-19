package l2s.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.model.base.Sex;
import l2s.gameserver.templates.player.transform.TransformTemplate;

/**
 * @author Bonux
**/
public final class TransformTemplateHolder extends AbstractHolder
{
	private static final TransformTemplateHolder _instance = new TransformTemplateHolder();

	private TIntObjectMap<TIntObjectMap<TransformTemplate>> _templates = new TIntObjectHashMap<TIntObjectMap<TransformTemplate>>();

	public TransformTemplateHolder()
	{
		for(Sex sex : Sex.VALUES)
			_templates.put(sex.ordinal(), new TIntObjectHashMap<TransformTemplate>());
	}

	public static TransformTemplateHolder getInstance()
	{
		return _instance;
	}

	public void addTemplate(Sex sex, TransformTemplate template)
	{
		_templates.get(sex.ordinal()).put(template.getId(), template);
	}

	public TransformTemplate getTemplate(Sex sex, int id)
	{
		return _templates.get(sex.ordinal()).get(id);
	}

	@Override
	public int size()
	{
		int size = 0;
		for(Sex sex : Sex.VALUES)
			size += _templates.get(sex.ordinal()).size();
		return size;
	}

	@Override
	public void clear()
	{
		_templates.clear();
	}
}