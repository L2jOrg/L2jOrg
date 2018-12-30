package org.l2j.gameserver.data.xml.holder;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.model.base.ResidenceFunctionType;
import org.l2j.gameserver.templates.residence.ResidenceFunctionTemplate;
import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

public final class ResidenceFunctionsHolder extends AbstractHolder
{
	private static final ResidenceFunctionsHolder _instance = new ResidenceFunctionsHolder();

	private final IntObjectMap<ResidenceFunctionTemplate> _templates = new HashIntObjectMap<ResidenceFunctionTemplate>();
	private final Map<ResidenceFunctionType, IntObjectMap<ResidenceFunctionTemplate>> _templatesByTypeAndLevel = new HashMap<ResidenceFunctionType, IntObjectMap<ResidenceFunctionTemplate>>(ResidenceFunctionType.VALUES.length);

	public static ResidenceFunctionsHolder getInstance()
	{
		return _instance;
	}

	public void addTemplate(ResidenceFunctionTemplate template)
	{
		_templates.put(template.getId(), template);

		IntObjectMap<ResidenceFunctionTemplate> templates = _templatesByTypeAndLevel.get(template.getType());
		if(templates == null)
		{
			templates = new HashIntObjectMap<ResidenceFunctionTemplate>();
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
		return _templates.values();
	}

	public ResidenceFunctionTemplate getTemplate(ResidenceFunctionType type, int level)
	{
		IntObjectMap<ResidenceFunctionTemplate> templates = _templatesByTypeAndLevel.get(type);
		if(templates == null)
			return null;

		return templates.get(level);
	}

	public Collection<ResidenceFunctionTemplate> getTemplates(ResidenceFunctionType type)
	{
		IntObjectMap<ResidenceFunctionTemplate> templates = _templatesByTypeAndLevel.get(type);
		if(templates == null)
			return Collections.emptyList();

		return templates.values();
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