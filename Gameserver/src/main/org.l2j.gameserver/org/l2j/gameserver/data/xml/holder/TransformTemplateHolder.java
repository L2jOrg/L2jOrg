package org.l2j.gameserver.data.xml.holder;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.model.base.Sex;
import org.l2j.gameserver.templates.player.transform.TransformTemplate;
import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

/**
 * @author Bonux
**/
public final class TransformTemplateHolder extends AbstractHolder
{
	private static final TransformTemplateHolder _instance = new TransformTemplateHolder();

	private IntObjectMap<IntObjectMap<TransformTemplate>> _templates = new HashIntObjectMap<>();

	public TransformTemplateHolder()
	{
		for(Sex sex : Sex.VALUES)
			_templates.put(sex.ordinal(), new HashIntObjectMap<TransformTemplate>());
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