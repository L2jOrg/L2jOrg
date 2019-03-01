package org.l2j.gameserver.data.xml.parser;

import org.dom4j.Element;
import org.l2j.commons.data.xml.AbstractParser;
import org.l2j.gameserver.data.xml.holder.ShuttleTemplateHolder;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.templates.ShuttleTemplate;
import org.l2j.gameserver.templates.ShuttleTemplate.ShuttleDoor;
import org.l2j.gameserver.templates.StatsSet;

import java.nio.file.Path;
import java.util.Iterator;

import static org.l2j.commons.configuration.Configurator.getSettings;

/**
 * @author Bonux
 */
public final class ShuttleTemplateParser extends AbstractParser<ShuttleTemplateHolder>
{
	private static final ShuttleTemplateParser _instance = new ShuttleTemplateParser();

	public static ShuttleTemplateParser getInstance()
	{
		return _instance;
	}

	protected ShuttleTemplateParser()
	{
		super(ShuttleTemplateHolder.getInstance());
	}

	@Override
	public Path getXMLPath() {
		return getSettings(ServerSettings.class).dataPackRootPath().resolve("data/shuttle_data.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "shuttle_data.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element shuttleElement = iterator.next();

			int shuttleId = Integer.parseInt(shuttleElement.attributeValue("id"));
			ShuttleTemplate template = new ShuttleTemplate(shuttleId);
			for(Iterator<Element> doorsIterator = shuttleElement.elementIterator("doors"); doorsIterator.hasNext();)
			{
				Element doorsElement = doorsIterator.next();
				for(Iterator<Element> doorIterator = doorsElement.elementIterator("door"); doorIterator.hasNext();)
				{
					Element doorElement = doorIterator.next();

					int doorId = Integer.parseInt(doorElement.attributeValue("id"));
					StatsSet set = new StatsSet();
					for(Iterator<Element> setIterator = doorElement.elementIterator("set"); setIterator.hasNext();)
					{
						Element setElement = setIterator.next();
						set.set(setElement.attributeValue("name"), setElement.attributeValue("value"));
					}
					template.addDoor(new ShuttleDoor(doorId, set));
				}

			}

			getHolder().addTemplate(template);
		}
	}
}