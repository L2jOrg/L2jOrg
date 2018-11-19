package l2s.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.model.base.ResidenceFunctionType;
import l2s.gameserver.templates.residence.ResidenceFunctionTemplate;

public final class ResidenceFunctionsHolder extends AbstractHolder
{
	private static final ResidenceFunctionsHolder _instance = new ResidenceFunctionsHolder();

	private final TIntObjectMap<ResidenceFunctionTemplate> _templates = new TIntObjectHashMap<ResidenceFunctionTemplate>();
	private final Map<ResidenceFunctionType, TIntObjectMap<ResidenceFunctionTemplate>> _templatesByTypeAndLevel = new HashMap<ResidenceFunctionType, TIntObjectMap<ResidenceFunctionTemplate>>(ResidenceFunctionType.VALUES.length);

	public static ResidenceFunctionsHolder getInstance()
	{
		return _instance;
	}

	public void addTemplate(ResidenceFunctionTemplate template)
	{
		_templates.put(template.getId(), template);

		TIntObjectMap<ResidenceFunctionTemplate> templates = _templatesByTypeAndLevel.get(template.getType());
		if(templates == null)
		{
			templates = new TIntObjectHashMap<ResidenceFunctionTemplate>();
			_templatesByTypeAndLevel.put(template.getType(), templates);
		}
		templates.put(template.getLevel(), template);
	}

	public ResidenceFunctionTemplate getTemplate(int id)
	{
		return _templates.get(id);
	}

	public Collection<ResidenceFunctionTemplate> getTemplates()
	{
		return _templates.valueCollection();
	}

	public ResidenceFunctionTemplate getTemplate(ResidenceFunctionType type, int level)
	{
		TIntObjectMap<ResidenceFunctionTemplate> templates = _templatesByTypeAndLevel.get(type);
		if(templates == null)
			return null;

		return templates.get(level);
	}

	public Collection<ResidenceFunctionTemplate> getTemplates(ResidenceFunctionType type)
	{
		TIntObjectMap<ResidenceFunctionTemplate> templates = _templatesByTypeAndLevel.get(type);
		if(templates == null)
			return Collections.emptyList();

		return templates.valueCollection();
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
		_templatesByTypeAndLevel.clear();
	}
}