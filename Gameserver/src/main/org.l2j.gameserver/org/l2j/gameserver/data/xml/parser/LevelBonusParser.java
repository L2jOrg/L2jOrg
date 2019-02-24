package org.l2j.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;
import org.l2j.commons.data.xml.AbstractParser;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.holder.LevelBonusHolder;
import org.l2j.gameserver.settings.ServerSettings;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author Bonux
**/
public final class LevelBonusParser extends AbstractParser<LevelBonusHolder>
{
	private static final LevelBonusParser _instance = new LevelBonusParser();

	public static LevelBonusParser getInstance()
	{
		return _instance;
	}

	private LevelBonusParser()
	{
		super(LevelBonusHolder.getInstance());
	}

	@Override
	public File getXMLPath() {
		return getSettings(ServerSettings.class).dataPackRootPath().resolve("data/pc_parameters/lvl_bonus_data.xml").toFile();
	}

	@Override
	public String getDTDFileName()
	{
		return "lvl_bonus_data.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element element = iterator.next();
			if("lvl_bonus".equalsIgnoreCase(element.getName()))
			{
				for(Element e : element.elements())
				{
					int lvl = Integer.parseInt(e.attributeValue("lvl"));
					double bonusMod = Double.parseDouble(e.attributeValue("value"));
					getHolder().addLevelBonus(lvl, bonusMod);
				}
			}
		}
	}
}