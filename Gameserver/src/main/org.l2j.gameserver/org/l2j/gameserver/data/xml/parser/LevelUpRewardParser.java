package org.l2j.gameserver.data.xml.parser;

import io.github.joealisson.primitive.maps.IntLongMap;
import io.github.joealisson.primitive.maps.impl.HashIntLongMap;
import org.dom4j.Element;
import org.l2j.commons.data.xml.AbstractParser;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.holder.LevelUpRewardHolder;

import java.io.File;
import java.util.Iterator;

/**
 * @author Bonux
**/
public final class LevelUpRewardParser extends AbstractParser<LevelUpRewardHolder>
{
	private static final LevelUpRewardParser _instance = new LevelUpRewardParser();

	public static LevelUpRewardParser getInstance()
	{
		return _instance;
	}

	private LevelUpRewardParser()
	{
		super(LevelUpRewardHolder.getInstance());
	}

	@Override
	public File getXMLPath()
	{
		return new File(Config.DATAPACK_ROOT, "data/pc_parameters/lvl_up_reward_data.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "lvl_up_reward_data.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element element = iterator.next();
			if("reward".equalsIgnoreCase(element.getName()))
			{
				int level = Integer.parseInt(element.attributeValue("level"));
				IntLongMap items = new HashIntLongMap();

				for(Element e : element.elements())
				{
					int id = Integer.parseInt(e.attributeValue("id"));
					long count = Long.parseLong(e.attributeValue("count"));

					items.put(id, count);
				}

				getHolder().addRewardData(level, items);
			}
		}
	}
}