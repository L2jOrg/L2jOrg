package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;
import l2s.commons.data.xml.AbstractParser;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.BaseStatsBonusHolder;
import l2s.gameserver.templates.BaseStatsBonus;

/**
 * @author Bonux
**/
public final class BaseStatsBonusParser extends AbstractParser<BaseStatsBonusHolder>
{
	private static final BaseStatsBonusParser _instance = new BaseStatsBonusParser();

	public static BaseStatsBonusParser getInstance()
	{
		return _instance;
	}

	private BaseStatsBonusParser()
	{
		super(BaseStatsBonusHolder.getInstance());
	}

	@Override
	public File getXMLPath()
	{
		return new File(Config.DATAPACK_ROOT, "data/pc_parameters/base_stats_bonus_data.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "base_stats_bonus_data.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element element = iterator.next();
			if("base_stats_bonus".equalsIgnoreCase(element.getName()))
			{
				for(Element e : element.elements())
				{
					int value = Integer.parseInt(e.attributeValue("value"));
					double str = (100. + Integer.parseInt(e.attributeValue("str"))) / 100;
					double _int = (100. + Integer.parseInt(e.attributeValue("int"))) / 100;
					double dex = (100. + Integer.parseInt(e.attributeValue("dex"))) / 100;
					double wit = (100. + Integer.parseInt(e.attributeValue("wit"))) / 100;
					double con = (100. + Integer.parseInt(e.attributeValue("con"))) / 100;
					double men = (100. + Integer.parseInt(e.attributeValue("men"))) / 100;

					getHolder().addBaseStatsBonus(value, new BaseStatsBonus(_int, str, con, men, dex, wit));
				}
			}
		}
	}
}