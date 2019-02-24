package org.l2j.gameserver.data.xml.parser;

import org.dom4j.Element;
import org.l2j.commons.data.xml.AbstractParser;
import org.l2j.commons.geometry.Polygon;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.instancemanager.MapRegionManager;
import org.l2j.gameserver.model.Territory;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.templates.mapregion.DomainArea;

import java.io.File;
import java.util.Iterator;

import static org.l2j.commons.configuration.Configurator.getSettings;

public class DomainParser extends AbstractParser<MapRegionManager>
{
	private static final DomainParser _instance = new DomainParser();

	public static DomainParser getInstance()
	{
		return _instance;
	}

	protected DomainParser()
	{
		super(MapRegionManager.getInstance());
	}

	@Override
	public File getXMLPath() {
		return getSettings(ServerSettings.class).dataPackRootPath().resolve("data/mapregion/domains.xml").toFile();
	}

	@Override
	public String getDTDFileName()
	{
		return "domains.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element listElement = iterator.next();

			if("domain".equals(listElement.getName()))
			{
				int id = Integer.parseInt(listElement.attributeValue("id"));
				Territory territory = null;

				for(Iterator<Element> i = listElement.elementIterator(); i.hasNext();)
				{
					Element n = i.next();

					if("polygon".equalsIgnoreCase(n.getName()))
					{
						Polygon shape = ZoneParser.parsePolygon(n);

						if(!shape.validate())
							logger.error("DomainParser: invalid territory data : " + shape + "!");

						if(territory == null)
							territory = new Territory();

						territory.add(shape);
					}
				}

				if(territory == null)
					throw new RuntimeException("DomainParser: empty territory!");

				getHolder().addRegionData(new DomainArea(id, territory));
			}
		}
	}
}
