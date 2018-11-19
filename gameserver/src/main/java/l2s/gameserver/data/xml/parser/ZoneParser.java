package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;
import l2s.commons.geometry.Circle;
import l2s.commons.geometry.Polygon;
import l2s.commons.geometry.Rectangle;
import l2s.commons.geometry.Shape;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.ZoneHolder;
import l2s.gameserver.model.Territory;
import l2s.gameserver.model.World;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.ZoneTemplate;
import l2s.gameserver.utils.Location;

/**
 * @author G1ta0
 */
public class ZoneParser extends StatParser<ZoneHolder>
{
	private static final ZoneParser _instance = new ZoneParser();

	public static ZoneParser getInstance()
	{
		return _instance;
	}

	protected ZoneParser()
	{
		super(ZoneHolder.getInstance());
	}

	@Override
	public File getXMLPath()
	{
		return new File(Config.DATAPACK_ROOT, "data/zone/");
	}

	@Override
	public File getCustomXMLPath()
	{
		return new File(Config.DATAPACK_ROOT, "custom/zone/");
	}

	@Override
	public String getDTDFileName()
	{
		return "zone.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator("zone"); iterator.hasNext();)
		{
			Element zoneElement = iterator.next();
			StatsSet zoneDat = new StatsSet();
			zoneDat.set("name", zoneElement.attributeValue("name"));
			zoneDat.set("type", zoneElement.attributeValue("type"));
			ZoneTemplate template = parseZone(zoneElement, zoneDat);
			if(template != null)
				getHolder().addTemplate(template);
		}
	}

	public static ZoneTemplate parseZone(Element zoneElement, StatsSet zoneDat) throws Exception
	{
		Territory territory = null;

		boolean isShape;

		for(Iterator<Element> i = zoneElement.elementIterator(); i.hasNext();)
		{
			Element n = i.next();
			if("set".equals(n.getName()))
				zoneDat.set(n.attributeValue("name"), n.attributeValue("value"));
			else if("restart_point".equals(n.getName()))
			{
				List<Location> restartPoints = new ArrayList<Location>();
				for(Iterator<Element> ii = n.elementIterator(); ii.hasNext();)
				{
					Element d = ii.next();
					if("coords".equalsIgnoreCase(d.getName()))
					{
						Location loc = Location.parseLoc(d.attribute("loc").getValue());
						restartPoints.add(loc);
					}
				}
				zoneDat.set("restart_points", restartPoints);
			}
			else if("PKrestart_point".equals(n.getName()))
			{
				List<Location> PKrestartPoints = new ArrayList<Location>();
				for(Iterator<Element> ii = n.elementIterator(); ii.hasNext();)
				{
					Element d = ii.next();
					if("coords".equalsIgnoreCase(d.getName()))
					{
						Location loc = Location.parseLoc(d.attribute("loc").getValue());
						PKrestartPoints.add(loc);
					}
				}
				zoneDat.set("PKrestart_points", PKrestartPoints);
			}
			else if((isShape = "rectangle".equalsIgnoreCase(n.getName())) || "banned_rectangle".equalsIgnoreCase(n.getName()))
			{
				Shape shape = parseRectangle(n);

				if(territory == null)
				{
					territory = new Territory();
					zoneDat.set("territory", territory);
				}

				if(isShape)
					territory.add(shape);
				else
					territory.addBanned(shape);
			}
			else if((isShape = "circle".equalsIgnoreCase(n.getName())) || "banned_cicrcle".equalsIgnoreCase(n.getName()))
			{
				Shape shape = parseCircle(n);

				if(territory == null)
				{
					territory = new Territory();
					zoneDat.set("territory", territory);
				}

				if(isShape)
					territory.add(shape);
				else
					territory.addBanned(shape);
			}
			else if((isShape = "polygon".equalsIgnoreCase(n.getName())) || "banned_polygon".equalsIgnoreCase(n.getName()))
			{
				Polygon shape = parsePolygon(n);

				if(!shape.validate())
					getInstance().error("ZoneParser: invalid territory data : " + shape + ", zone: " + zoneDat.getString("name") + "!");

				if(territory == null)
				{
					territory = new Territory();
					zoneDat.set("territory", territory);
				}

				if(isShape)
					territory.add(shape);
				else
					territory.addBanned(shape);
			}
			else if((isShape = "map".equalsIgnoreCase(n.getName())) || "banned_map".equalsIgnoreCase(n.getName()))
			{
				Shape shape = parseMap(n);

				if(territory == null)
				{
					territory = new Territory();
					zoneDat.set("territory", territory);
				}

				if(isShape)
					territory.add(shape);
				else
					territory.addBanned(shape);
			}
		}

		if(territory == null || territory.getTerritories().isEmpty())
		{
			getInstance().error("Empty territory for zone: " + zoneDat.get("name"));
			return null;
		}

		ZoneTemplate template = new ZoneTemplate(zoneDat);

		for(Iterator<Element> i = zoneElement.elementIterator("for"); i.hasNext();)
			getInstance().parseFor(i.next(), template);

		return template;
	}

	public static Rectangle parseRectangle(Element n) throws Exception
	{
		int x1, y1, x2, y2, zmin = World.MAP_MIN_Z, zmax = World.MAP_MAX_Z;

		Iterator<Element> i = n.elementIterator();

		Element d = i.next();
		String[] coord = d.attributeValue("loc").split("[\\s,;]+");
		x1 = Integer.parseInt(coord[0]);
		y1 = Integer.parseInt(coord[1]);
		if(coord.length > 2)
		{
			zmin = Integer.parseInt(coord[2]);
			zmax = Integer.parseInt(coord[3]);
		}

		d = i.next();
		coord = d.attributeValue("loc").split("[\\s,;]+");
		x2 = Integer.parseInt(coord[0]);
		y2 = Integer.parseInt(coord[1]);
		if(coord.length > 2)
		{
			zmin = Integer.parseInt(coord[2]);
			zmax = Integer.parseInt(coord[3]);
		}

		Rectangle rectangle = new Rectangle(x1, y1, x2, y2);
		rectangle.setZmin(zmin);
		rectangle.setZmax(zmax);

		return rectangle;
	}

	public static Polygon parsePolygon(Element shape) throws Exception
	{
		Polygon poly = new Polygon();

		for(Iterator<Element> i = shape.elementIterator(); i.hasNext();)
		{
			Element d = i.next();
			if("coords".equals(d.getName()))
			{
				String[] coord = d.attributeValue("loc").split("[\\s,;]+");
				if(coord.length < 3) // Не указаны minZ и maxZ, берем граничные значения
					poly.add(Integer.parseInt(coord[0]), Integer.parseInt(coord[1])).setZmin(World.MAP_MIN_Z).setZmax(World.MAP_MAX_Z);
				else if(coord.length < 4) // Не указаны minZ, берем граничные значения
					poly.add(Integer.parseInt(coord[0]), Integer.parseInt(coord[1])).setZmin(Integer.parseInt(coord[2])).setZmax(World.MAP_MAX_Z);
				else
					poly.add(Integer.parseInt(coord[0]), Integer.parseInt(coord[1])).setZmin(Integer.parseInt(coord[2])).setZmax(Integer.parseInt(coord[3]));
			}
		}

		return poly;
	}

	public static Circle parseCircle(Element shape) throws Exception
	{
		Circle circle;

		String[] coord = shape.attribute("loc").getValue().split("[\\s,;]+");
		if(coord.length < 4) // Не указаны minZ и maxZ, берем граничные значения
			circle = new Circle(Integer.parseInt(coord[0]), Integer.parseInt(coord[1]), Integer.parseInt(coord[2])).setZmin(World.MAP_MIN_Z).setZmax(World.MAP_MAX_Z);
		else if(coord.length < 5) // Не указаны minZ , берем граничные значения
			circle = new Circle(Integer.parseInt(coord[0]), Integer.parseInt(coord[1]), Integer.parseInt(coord[2])).setZmin(Integer.parseInt(coord[3])).setZmax(World.MAP_MAX_Z);
		else
			circle = new Circle(Integer.parseInt(coord[0]), Integer.parseInt(coord[1]), Integer.parseInt(coord[2])).setZmin(Integer.parseInt(coord[3])).setZmax(Integer.parseInt(coord[4]));

		return circle;
	}

	public static Rectangle parseMap(Element n) throws Exception
	{
		String[] map = n.attributeValue("value").split("_");

		int rx = Integer.parseInt(map[0]);
		int ry = Integer.parseInt(map[1]);

		int x1 = World.MAP_MIN_X + (rx - Config.GEO_X_FIRST << 15);
		int y1 = World.MAP_MIN_Y + (ry - Config.GEO_Y_FIRST << 15);
		int x2 = x1 + (1 << 15) - 1;
		int y2 = y1 + (1 << 15) - 1;

		Rectangle rectangle = new Rectangle(x1, y1, x2, y2);
		rectangle.setZmin(World.MAP_MIN_Z);
		rectangle.setZmax(World.MAP_MAX_Z);

		return rectangle;
	}

	@Override
	protected Object getTableValue(String name, int... arg)
	{
		return null;
	}
}