package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.Iterator;

import org.dom4j.Element;
import l2s.commons.data.xml.AbstractParser;
import l2s.commons.geometry.Polygon;
import l2s.gameserver.Config;
import l2s.gameserver.instancemanager.MapRegionManager;
import l2s.gameserver.model.Territory;
import l2s.gameserver.templates.mapregion.DomainArea;

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
	public File getXMLPath()
	{
		return new File(Config.DATAPACK_ROOT, "data/mapregion/domains.xml");
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
							error("DomainParser: invalid territory data : " + shape + "!");

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
