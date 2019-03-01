package org.l2j.gameserver.data.xml.parser;

import org.dom4j.Element;
import org.l2j.commons.data.xml.AbstractParser;
import org.l2j.commons.geometry.Polygon;
import org.l2j.gameserver.data.xml.holder.DoorHolder;
import org.l2j.gameserver.settings.ServerSettings;
import org.l2j.gameserver.templates.DoorTemplate;
import org.l2j.gameserver.templates.StatsSet;
import org.l2j.gameserver.utils.Location;

import java.nio.file.Path;
import java.util.Iterator;

import static org.l2j.commons.configuration.Configurator.getSettings;

public final class DoorParser extends AbstractParser<DoorHolder>
{
	private static final DoorParser _instance = new DoorParser();

	public static DoorParser getInstance()
	{
		return _instance;
	}

	protected DoorParser()
	{
		super(DoorHolder.getInstance());
	}

	@Override
	public Path getXMLPath() {
		return getSettings(ServerSettings.class).dataPackRootPath().resolve("data/doors/");
	}

	@Override
	public String getDTDFileName()
	{
		return "doors.dtd";
	}

	private StatsSet initBaseStats()
	{
		StatsSet baseDat = new StatsSet();
		baseDat.set("level", 0);
		baseDat.set("baseSTR", 0);
		baseDat.set("baseCON", 0);
		baseDat.set("baseDEX", 0);
		baseDat.set("baseINT", 0);
		baseDat.set("baseWIT", 0);
		baseDat.set("baseMEN", 0);
		baseDat.set("baseShldDef", 0);
		baseDat.set("baseShldRate", 0);
		baseDat.set("basePCritRate", 0);
		baseDat.set("baseMCritRate", 0);
		baseDat.set("baseAtkRange", 0);
		baseDat.set("baseMpMax", 0);
		baseDat.set("baseCpMax", 0);
		baseDat.set("basePAtk", 0);
		baseDat.set("baseMAtk", 0);
		baseDat.set("basePAtkSpd", 0);
		baseDat.set("baseMAtkSpd", 0);
		baseDat.set("baseWalkSpd", 0);
		baseDat.set("baseRunSpd", 0);
		baseDat.set("baseHpReg", 0);
		baseDat.set("baseCpReg", 0);
		baseDat.set("baseMpReg", 0);

		return baseDat;
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		for(Iterator<Element> iterator = rootElement.elementIterator(); iterator.hasNext();)
		{
			Element doorElement = iterator.next();

			if("door".equals(doorElement.getName()))
			{
				StatsSet doorSet = initBaseStats();
				StatsSet aiParams = null;

				doorSet.set("door_type", doorElement.attributeValue("type"));

				Element posElement = doorElement.element("pos");
				Location doorPos;
				int x = Integer.parseInt(posElement.attributeValue("x"));
				int y = Integer.parseInt(posElement.attributeValue("y"));
				int z = Integer.parseInt(posElement.attributeValue("z"));
				doorSet.set("pos", doorPos = new Location(x, y, z));

				Polygon shape = new Polygon();
				int minz = 0, maxz = 0;

				Element shapeElement = doorElement.element("shape");
				minz = Integer.parseInt(shapeElement.attributeValue("minz"));
				maxz = Integer.parseInt(shapeElement.attributeValue("maxz"));
				shape.add(Integer.parseInt(shapeElement.attributeValue("ax")), Integer.parseInt(shapeElement.attributeValue("ay")));
				shape.add(Integer.parseInt(shapeElement.attributeValue("bx")), Integer.parseInt(shapeElement.attributeValue("by")));
				shape.add(Integer.parseInt(shapeElement.attributeValue("cx")), Integer.parseInt(shapeElement.attributeValue("cy")));
				shape.add(Integer.parseInt(shapeElement.attributeValue("dx")), Integer.parseInt(shapeElement.attributeValue("dy")));
				shape.setZmin(minz);
				shape.setZmax(maxz);
				doorSet.set("shape", shape);

				doorPos.setZ(minz + 32); //фактическая координата двери в мире

				for(Iterator<Element> i = doorElement.elementIterator(); i.hasNext();)
				{
					Element n = i.next();
					if("set".equals(n.getName()))
						doorSet.set(n.attributeValue("name"), n.attributeValue("value"));
					else if("ai_params".equals(n.getName()))
					{
						if(aiParams == null)
						{
							aiParams = new StatsSet();
							doorSet.set("ai_params", aiParams);
						}

						for(Iterator<Element> aiParamsIterator = n.elementIterator(); aiParamsIterator.hasNext();)
						{
							Element aiParamElement = aiParamsIterator.next();

							aiParams.set(aiParamElement.attributeValue("name"), aiParamElement.attributeValue("value"));
						}
					}
				}

				doorSet.set("uid", doorElement.attributeValue("id"));
				doorSet.set("name", doorElement.attributeValue("name"));
				doorSet.set("baseHpMax", doorElement.attributeValue("hp"));
				doorSet.set("basePDef", doorElement.attributeValue("pdef"));
				doorSet.set("baseMDef", doorElement.attributeValue("mdef"));

				doorSet.set("collision_height", maxz - minz & 0xfff0);
				doorSet.set("collision_radius", Math.max(50, Math.min(doorPos.x - shape.getXmin(), doorPos.y - shape.getYmin())));

				DoorTemplate template = new DoorTemplate(doorSet);
				getHolder().addTemplate(template);
			}
		}
	}
}
