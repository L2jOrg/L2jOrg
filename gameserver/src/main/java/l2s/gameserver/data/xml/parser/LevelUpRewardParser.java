package l2s.gameserver.data.xml.parser;

import gnu.trove.map.TIntLongMap;
import gnu.trove.map.hash.TIntLongHashMap;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;
import l2s.commons.data.xml.AbstractParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.LevelUpRewardHolder;

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
				TIntLongMap items = new TIntLongHashMap();

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