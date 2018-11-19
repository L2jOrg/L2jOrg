package l2s.gameserver.data.xml.parser;

import java.io.File;
import java.util.*;

import org.dom4j.Element;
import l2s.gameserver.templates.npc.MinionData;
import l2s.commons.collections.MultiValueSet;
import l2s.commons.data.xml.AbstractParser;
import l2s.commons.geometry.Circle;
import l2s.commons.geometry.Polygon;
import l2s.commons.geometry.Rectangle;
import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.SpawnHolder;
import l2s.gameserver.model.Territory;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.spawn.PeriodOfDay;
import l2s.gameserver.templates.spawn.SpawnNpcInfo;
import l2s.gameserver.templates.spawn.SpawnTemplate;
import l2s.gameserver.utils.Location;

/**
 * @author VISTALL
 * @date  18:38/10.12.2010
 */
public final class SpawnParser extends AbstractParser<SpawnHolder>
{
	private static final SpawnParser _instance = new SpawnParser();

	public static SpawnParser getInstance()
	{
		return _instance;
	}

	protected SpawnParser()
	{
		super(SpawnHolder.getInstance());
	}

	@Override
	public File getXMLPath()
	{
		return new File(Config.DATAPACK_ROOT, "data/spawn/");
	}

	@Override
	public File getCustomXMLPath()
	{
		return new File(Config.DATAPACK_ROOT, "custom/spawn/");
	}

	@Override
	public String getDTDFileName()
	{
		return "spawn.dtd";
	}

	@Override
	protected void readData(Element rootElement) throws Exception
	{
		Map<String, Territory> territories = new HashMap<String, Territory>();
		for(Iterator<Element> spawnIterator = rootElement.elementIterator(); spawnIterator.hasNext();)
		{
			Element spawnElement = spawnIterator.next();
			if(spawnElement.getName().equalsIgnoreCase("territory"))
			{
				String terName = spawnElement.attributeValue("name");
				Territory territory = parseTerritory(terName, spawnElement);
				territories.put(terName, territory);
			}
			else if(spawnElement.getName().equalsIgnoreCase("spawn"))
			{
				String group = spawnElement.attributeValue("group");
				String name = spawnElement.attributeValue("name") == null ? (group == null ? "" : group) : spawnElement.attributeValue("name");
				int respawn = spawnElement.attributeValue("respawn") == null ? 60 : Integer.parseInt(spawnElement.attributeValue("respawn"));
				int respawnRandom = spawnElement.attributeValue("respawn_random") == null ? 0 : Integer.parseInt(spawnElement.attributeValue("respawn_random"));
				String respawnPattern = spawnElement.attributeValue("respawn_pattern");
				int count = spawnElement.attributeValue("count") == null ? 1 : Integer.parseInt(spawnElement.attributeValue("count"));
				PeriodOfDay periodOfDay = spawnElement.attributeValue("period_of_day") == null ? PeriodOfDay.NONE : PeriodOfDay.valueOf(spawnElement.attributeValue("period_of_day").toUpperCase());
				if(group == null)
					group = periodOfDay.name();
				SpawnTemplate template = new SpawnTemplate(name, periodOfDay, count, respawn, respawnRandom, respawnPattern);

				for(Iterator<Element> subIterator = spawnElement.elementIterator(); subIterator.hasNext();)
				{
					Element subElement = subIterator.next();
					if(subElement.getName().equalsIgnoreCase("point"))
					{
						int x = Integer.parseInt(subElement.attributeValue("x"));
						int y = Integer.parseInt(subElement.attributeValue("y"));
						int z = Integer.parseInt(subElement.attributeValue("z"));
						int h = subElement.attributeValue("h") == null ? -1 : Integer.parseInt(subElement.attributeValue("h"));

						template.addSpawnRange(new Location(x, y, z, h));
					}
					else if(subElement.getName().equalsIgnoreCase("rectangle"))
					{
						int x1 = Integer.parseInt(subElement.attributeValue("x1"));
						int y1 = Integer.parseInt(subElement.attributeValue("y1"));
						int x2 = Integer.parseInt(subElement.attributeValue("x2"));
						int y2 = Integer.parseInt(subElement.attributeValue("y2"));
						int zmin = Integer.parseInt(subElement.attributeValue("zmin"));
						int zmax = Integer.parseInt(subElement.attributeValue("zmax"));

						Rectangle rectangle = new Rectangle(x1, y1, x2, y2);
						rectangle.setZmin(zmin);
						rectangle.setZmax(zmax);

						Territory territory = new Territory();
						territory.add(rectangle);

						template.addSpawnRange(territory);
					}
					else if(subElement.getName().equalsIgnoreCase("circle"))
					{
						int x = Integer.parseInt(subElement.attributeValue("x"));
						int y = Integer.parseInt(subElement.attributeValue("y"));
						int zmin = Integer.parseInt(subElement.attributeValue("zmin"));
						int zmax = Integer.parseInt(subElement.attributeValue("zmax"));
						int radius = Integer.parseInt(subElement.attributeValue("radius"));

						Circle circle = new Circle(x, y, radius);
						circle.setZmin(zmin);
						circle.setZmax(zmax);

						Territory territory = new Territory();
						territory.add(circle);

						template.addSpawnRange(territory);
					}
					else if(subElement.getName().equalsIgnoreCase("territory"))
					{
						String terName = subElement.attributeValue("name");
						if(terName != null)
						{
							Territory g = territories.get(terName);
							if(g == null)
							{
								error("Invalid territory name: " + terName + "; " + getCurrentFileName());
								continue;
							}
							template.addSpawnRange(g);
						}
						else
						{
							Territory temp = parseTerritory(null, subElement);

							template.addSpawnRange(temp);
						}
					}
					else if(subElement.getName().equalsIgnoreCase("npc"))
					{
						int npcId = Integer.parseInt(subElement.attributeValue("id"));
						int max = subElement.attributeValue("max") == null ? 0 : Integer.parseInt(subElement.attributeValue("max"));
						MultiValueSet<String> parameters = StatsSet.EMPTY;
						List<MinionData> minions = Collections.emptyList();

						String ai = subElement.attributeValue("ai");
						if(ai != null)
						{
							if(parameters.isEmpty())
								parameters = new MultiValueSet<String>();
							parameters.set("ai_type", ai);
						}

						for(Iterator<Element> npcIterator = subElement.elementIterator(); npcIterator.hasNext();)
						{
							Element npcElement = npcIterator.next();
							if(npcElement.getName().equalsIgnoreCase("set"))
							{
								if(parameters.isEmpty())
									parameters = new MultiValueSet<String>();
								parameters.set(npcElement.attributeValue("name"), npcElement.attributeValue("value"));
							}
							else if(npcElement.getName().equalsIgnoreCase("minions"))
							{
								for(Iterator<Element> nextIterator = npcElement.elementIterator(); nextIterator.hasNext();)
								{
									Element nextElement = nextIterator.next();
									int minionId = Integer.parseInt(nextElement.attributeValue("npc_id"));
									String minionAi = nextElement.attributeValue("ai");
									int minionCount = Integer.parseInt(nextElement.attributeValue("count"));
									int minionRespawn = nextElement.attributeValue("respawn") == null ? -1 : Integer.parseInt(nextElement.attributeValue("respawn"));

									if(minions.isEmpty())
										minions = new ArrayList<MinionData>();

									minions.add(new MinionData(minionId, minionAi, minionCount, minionRespawn));
								}
							}
						}

						template.addNpc(new SpawnNpcInfo(npcId, max, parameters, minions));
					}
				}

				if(template.getNpcList().size() == 0)
				{
					warn("Npc id is zero! File: " + getCurrentFileName());
					continue;
				}

				if(template.getSpawnRangeList().size() == 0)
				{
					warn("No points to spawn! File: " + getCurrentFileName());
					continue;
				}

				getHolder().addSpawn(group, template);
			}
		}
	}

	private Territory parseTerritory(String name, Element e)
	{
		Territory t = new Territory();
		t.add(parsePolygon0(name, e));

		for(Iterator<Element> iterator = e.elementIterator("banned_territory"); iterator.hasNext();)
			t.addBanned(parsePolygon0(name, iterator.next()));

		return t;
	}

	private Polygon parsePolygon0(String name, Element e)
	{
		Polygon temp = new Polygon();
		for(Iterator<Element> addIterator = e.elementIterator("add"); addIterator.hasNext();)
		{
			Element addElement = addIterator.next();
			int x = Integer.parseInt(addElement.attributeValue("x"));
			int y = Integer.parseInt(addElement.attributeValue("y"));
			int zmin = Integer.parseInt(addElement.attributeValue("zmin"));
			int zmax = Integer.parseInt(addElement.attributeValue("zmax"));
			temp.add(x, y).setZmin(zmin).setZmax(zmax);
		}

		if(!temp.validate())
			error("Invalid polygon: " + name + "{" + temp + "}. File: " + getCurrentFileName());
		return temp;
	}
}
