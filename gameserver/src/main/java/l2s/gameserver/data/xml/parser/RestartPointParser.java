package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.dom4j.Attribute;
import org.dom4j.Element;
import l2s.commons.data.xml.AbstractParser;
import l2s.commons.geometry.Polygon;
import l2s.commons.geometry.Rectangle;
import l2s.gameserver.Config;
import l2s.gameserver.instancemanager.MapRegionManager;
import l2s.gameserver.model.Territory;
import l2s.gameserver.model.World;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.templates.mapregion.RestartArea;
import l2s.gameserver.templates.mapregion.RestartPoint;
import l2s.gameserver.utils.Location;

public class RestartPointParser extends AbstractParser<MapRegionManager>
{
	private static final RestartPointParser _instance = new RestartPointParser();

	public static RestartPointParser getInstance()
	{
		return _instance;
	}

	private RestartPointParser()
	{
		super(MapRegionManager.getInstance());
	}

	@Override
	public File getXMLPath()
	{
		return new File(Config.DATAPACK_ROOT, "data/mapregion/restart_points.xml");
	}

	@Override
	public String getDTDFileName()
	{
		return "restart_points.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		List<Pair<Territory, Map<Race, String>>> restartArea = new ArrayList<Pair<Territory, Map<Race, String>>>();
		Map<String, RestartPoint> restartPoint = new HashMap<String, RestartPoint>();

		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element listElement = iterator.next();

			if("restart_area".equals(listElement.getName()))
			{
				Territory territory = null;
				Map<Race, String> restarts = new HashMap<Race, String>();

				for(Iterator<Element> i = listElement.elementIterator(); i.hasNext();)
				{
					Element n = i.next();

					if("region".equalsIgnoreCase(n.getName()))
					{
						Rectangle shape;

						Attribute map = n.attribute("map");
						String s = map.getValue();
						String val[] = s.split("_");
						int rx = Integer.parseInt(val[0]);
						int ry = Integer.parseInt(val[1]);

						int x1 = World.MAP_MIN_X + (rx - Config.GEO_X_FIRST << 15);
						int y1 = World.MAP_MIN_Y + (ry - Config.GEO_Y_FIRST << 15);
						int x2 = x1 + (1 << 15) - 1;
						int y2 = y1 + (1 << 15) - 1;

						shape = new Rectangle(x1, y1, x2, y2);
						shape.setZmin(World.MAP_MIN_Z);
						shape.setZmax(World.MAP_MAX_Z);

						if(territory == null)
							territory = new Territory();

						territory.add(shape);
					}
					else if("polygon".equalsIgnoreCase(n.getName()))
					{
						Polygon shape = ZoneParser.parsePolygon(n);

						if(!shape.validate())
							error("RestartPointParser: invalid territory data : " + shape + "!");

						if(territory == null)
							territory = new Territory();

						territory.add(shape);
					}
					else if("restart".equalsIgnoreCase(n.getName()))
					{
						Race race = Race.valueOf(n.attributeValue("race").toUpperCase());
						String locName = n.attributeValue("loc");
						restarts.put(race, locName);
					}
				}

				if(territory == null)
					throw new RuntimeException("RestartPointParser: empty territory!");

				if(restarts.isEmpty())
					throw new RuntimeException("RestartPointParser: restarts not defined!");

				restartArea.add(new ImmutablePair<Territory, Map<Race, String>>(territory, restarts));
			}
			else if("restart_loc".equals(listElement.getName()))
			{
				String name = listElement.attributeValue("name");
				int bbs = Integer.parseInt(listElement.attributeValue("bbs", "0"));
				int msgId = Integer.parseInt(listElement.attributeValue("msg_id", "0"));
				List<Location> restartPoints = new ArrayList<Location>();
				List<Location> PKrestartPoints = new ArrayList<Location>();

				for(Iterator<Element> i = listElement.elementIterator(); i.hasNext();)
				{
					Element n = i.next();
					if("restart_point".equals(n.getName()))
					{
						for(Iterator<Element> ii = n.elementIterator(); ii.hasNext();)
						{
							Element d = ii.next();
							if("coords".equalsIgnoreCase(d.getName()))
							{
								Location loc = Location.parseLoc(d.attribute("loc").getValue());
								restartPoints.add(loc);
							}
						}
					}
					else if("PKrestart_point".equals(n.getName()))
					{
						for(Iterator<Element> ii = n.elementIterator(); ii.hasNext();)
						{
							Element d = ii.next();
							if("coords".equalsIgnoreCase(d.getName()))
							{
								Location loc = Location.parseLoc(d.attribute("loc").getValue());
								PKrestartPoints.add(loc);
							}
						}
					}
				}

				if(restartPoints.isEmpty())
					throw new RuntimeException("RestartPointParser: restart_points not defined for restart_loc : " + name + "!");

				if(PKrestartPoints.isEmpty())
					PKrestartPoints = restartPoints;

				RestartPoint rp = new RestartPoint(name, bbs, msgId, restartPoints, PKrestartPoints);
				restartPoint.put(name, rp);
			}
		}

		for(Pair<Territory, Map<Race, String>> ra : restartArea)
		{
			Map<Race, RestartPoint> restarts = new HashMap<Race, RestartPoint>();

			for(Map.Entry<Race, String> e : ra.getValue().entrySet())
			{
				RestartPoint rp = restartPoint.get(e.getValue());
				if(rp == null)
					throw new RuntimeException("RestartPointParser: restart_loc not found : " + e.getValue() + "!");

				restarts.put(e.getKey(), rp);

				getHolder().addRegionData(new RestartArea(ra.getKey(), restarts));
			}
		}
	}
}
