package org.l2j.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;
import org.l2j.commons.data.xml.AbstractParser;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.holder.KarmaIncreaseDataHolder;
import org.l2j.gameserver.settings.ServerSettings;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author Bonux
**/
public final class KarmaIncreaseDataParser extends AbstractParser<KarmaIncreaseDataHolder>
{
	private static final KarmaIncreaseDataParser _instance = new KarmaIncreaseDataParser();

	public static KarmaIncreaseDataParser getInstance()
	{
		return _instance;
	}

	private KarmaIncreaseDataParser()
	{
		super(KarmaIncreaseDataHolder.getInstance());
	}

	@Override
	public File getXMLPath() {
		return getSettings(ServerSettings.class).dataPackRootPath().resolve("data/pc_parameters/pc_karma_increase_data.xml").toFile();
	}

	@Override
	public String getDTDFileName()
	{
		return "pc_karma_increase_data.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element element = iterator.next();
			if("parameters".equalsIgnoreCase(element.getName()))
			{
				Config.KARMA_PENALTY_START_KARMA = Integer.parseInt(element.attributeValue("penalty_start_karma"));
				Config.KARMA_PENALTY_DURATION_DEFAULT = Integer.parseInt(element.attributeValue("penalty_duration_default"));
				Config.KARMA_PENALTY_DURATION_INCREASE = Double.parseDouble(element.attributeValue("penalty_duration_increase"));
				Config.KARMA_DOWN_TIME_MULTIPLE = Integer.parseInt(element.attributeValue("down_time_multiple"));
				Config.KARMA_CRIMINAL_DURATION_MULTIPLE = Integer.parseInt(element.attributeValue("criminal_duration_multiple"));
			}
			else if("table".equalsIgnoreCase(element.getName()))
			{
				for(Element e : element.elements())
				{
					int lvl = Integer.parseInt(e.attributeValue("lvl"));
					double value = Double.parseDouble(e.attributeValue("value"));
					getHolder().addData(lvl, value);
				}
			}
		}
	}
}