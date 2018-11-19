package l2s.gameserver.templates.fakeplayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.ZoneHolder;
import l2s.gameserver.data.xml.parser.ZoneParser;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.Zone.ZoneType;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.templates.StatsSet;
import l2s.gameserver.templates.ZoneTemplate;
import l2s.gameserver.templates.fakeplayer.actions.GoToTownActions;
import l2s.gameserver.templates.fakeplayer.actions.OrdinaryActions;
import l2s.gameserver.utils.Location;

import org.dom4j.Element;
import org.napile.primitive.sets.IntSet;
import org.napile.primitive.sets.impl.HashIntSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FarmZoneTemplate
{
	private static final Logger _log = LoggerFactory.getLogger(FakePlayerAITemplate.class);
	private final int _minLevel;
	private final int _maxLevel;
	private final List<ZoneTemplate> _zoneTemplates;
	private final List<Location> _spawnPoints;
	private final OrdinaryActions _onObtainMaxLevelAction;
	private final GoToTownActions _goToTownAction;
	private final Set<ClassId> _availableClasses;
	private final Set<ClassType> _availableTypes;
	private final Set<Race> _availableRaces;
	private final IntSet _farmMonsters;
	private final IntSet _ignoredMonsters;

	public FarmZoneTemplate(int minLevel, int maxLevel, List<ZoneTemplate> zoneTemplates, List<Location> spawnPoints, OrdinaryActions onObtainMaxLevelAction, GoToTownActions goToTownAction, Set<ClassId> availableClasses, Set<ClassType> availableTypes, Set<Race> availableRaces, IntSet farmMonsters, IntSet ignoredMonsters)
	{
		_minLevel = minLevel;
		_maxLevel = maxLevel;
		_zoneTemplates = zoneTemplates;
		_spawnPoints = spawnPoints;
		_onObtainMaxLevelAction = onObtainMaxLevelAction;
		_goToTownAction = goToTownAction;
		_availableClasses = availableClasses;
		_availableRaces = availableRaces;
		_availableTypes = availableTypes;
		_farmMonsters = farmMonsters;
		_ignoredMonsters = ignoredMonsters;
	}

	public int getMinLevel()
	{
		return _minLevel;
	}

	public int getMaxLevel()
	{
		return _maxLevel;
	}

	public boolean checkCondition(Player player)
	{
		if(player.getLevel() < _minLevel)
			return false;
		if(player.getLevel() >= _maxLevel)
			return false;
		if(_availableClasses != null && !_availableClasses.isEmpty() && !_availableClasses.contains(player.getClassId()))
			return false;
		if(_availableRaces != null && !_availableRaces.isEmpty() && !_availableRaces.contains(player.getRace()))
			return false;
		if(_availableTypes != null && !_availableTypes.isEmpty() && !_availableTypes.contains(player.getRace()))
			return false;
		return true;
	}

	public List<ZoneTemplate> getZoneTemplates()
	{
		return _zoneTemplates;
	}

	public List<Location> getSpawnPoints()
	{
		return _spawnPoints;
	}

	public OrdinaryActions getOnObtainMaxLevelAction()
	{
		return _onObtainMaxLevelAction;
	}

	public GoToTownActions getGoToTownActions()
	{
		return _goToTownAction;
	}

	public IntSet getFarmMonsters()
	{
		return _farmMonsters;
	}

	public boolean isIgnoredMonster(int npcId)
	{
		return _ignoredMonsters.contains(npcId);
	}

	public static FarmZoneTemplate parse(FakePlayerActionsHolder actionsHolder, Element element)
	{
		Element tempElement = element.element("zones");
		if(tempElement == null)
		{
			_log.warn("Cannot find \"zones\" element!");
			return null;
		}

		String name = element.attributeValue("name");
		int minLevel = element.attributeValue("min_level") == null ? 1 : Integer.parseInt(element.attributeValue("min_level"));
		int maxLevel = element.attributeValue("max_level") == null ? Config.ALT_MAX_LEVEL + 1 : Integer.parseInt(element.attributeValue("max_level"));

		Set<ClassId> availableClasses = null;

		String classes = element.attributeValue("classes");
		if(classes != null)
		{
			availableClasses = new HashSet<ClassId>();
			for(String c : classes.split("[\\s,;]+"))
				availableClasses.add(ClassId.valueOf(c.toUpperCase()));
		}

		Set<ClassType> availableTypes = null;

		String types = element.attributeValue("types");
		if(types != null)
		{
			availableTypes = new HashSet<ClassType>();
			for(String t : types.split("[\\s,;]+"))
				availableTypes.add(ClassType.valueOf(t.toUpperCase()));
		}

		Set<Race> availableRaces = null;

		String races = element.attributeValue("races");
		if(races != null)
		{
			availableRaces = new HashSet<Race>();
			for(String r : races.split("[\\s,;]+"))
				availableRaces.add(Race.valueOf(r.toUpperCase()));
		}

		OrdinaryActions onObtainMaxLevelAction = null;
		GoToTownActions goToTownAction = null;

		List<ZoneTemplate> zoneTemplates = new ArrayList<ZoneTemplate>();
		for(Iterator<Element> i1 = tempElement.elementIterator("zone"); i1.hasNext();)
		{
			Element e1 = i1.next();

			try
			{
				String zoneName = e1.attributeValue("name");
				ZoneTemplate zoneTemplate;
				if(zoneName != null)
					zoneTemplate = ZoneHolder.getInstance().getTemplate(zoneName);
				else
				{
					StatsSet zoneDat = new StatsSet();
					zoneDat.set("name", name);
					zoneDat.set("type", ZoneType.dummy.toString());
					zoneTemplate = ZoneParser.parseZone(e1, zoneDat);
				}

				if(zoneTemplate != null)
					zoneTemplates.add(zoneTemplate);
			}
			catch(Exception e)
			{
				_log.error("Error while parse zone: ", e);
				return null;
			}
		}

		if(zoneTemplates.isEmpty())
		{
			_log.warn("Zones is empty! Please add one or more zones for farm zone.");
			return null;
		}

		List<Location> spawnPoints = new ArrayList<Location>();
		for(Iterator<Element> i1 = tempElement.elementIterator("spawn_points"); i1.hasNext();)
		{
			Element e1 = i1.next();
			for(Iterator<Element> i2 = e1.elementIterator("coords"); i2.hasNext();)
			{
				Element e2 = i2.next();
				spawnPoints.add(Location.parseLoc(e2.attribute("loc").getValue()));
			}
		}

		if(spawnPoints.isEmpty())
		{
			_log.warn("Spawn points for zones is empty! Please add one or more spawn points for farm zone.");
			return null;
		}

		if(actionsHolder == null)
			actionsHolder = new FakePlayerActionsHolder();

		tempElement = element.element("on_obtain_max_level");
		if(tempElement != null)
			onObtainMaxLevelAction = OrdinaryActions.parse(actionsHolder, tempElement);

		tempElement = element.element("go_to_town");
		if(tempElement != null)
			goToTownAction = GoToTownActions.parse(actionsHolder, tempElement);

		IntSet farmMonsters = new HashIntSet();
		for(Iterator<Element> i1 = element.elementIterator("farm_monsters"); i1.hasNext();)
		{
			Element e1 = i1.next();
			for(Iterator<Element> i2 = e1.elementIterator("npc"); i2.hasNext();)
			{
				Element e2 = i2.next();
				farmMonsters.add(Integer.parseInt(e2.attributeValue("id")));
			}
		}

		IntSet ignoredMonsters = new HashIntSet();
		for(Iterator<Element> i1 = element.elementIterator("ignored_monsters"); i1.hasNext();)
		{
			Element e1 = i1.next();
			for(Iterator<Element> i2 = e1.elementIterator("npc"); i2.hasNext();)
			{
				Element e2 = i2.next();
				ignoredMonsters.add(Integer.parseInt(e2.attributeValue("id")));
			}
		}

		FarmZoneTemplate template = new FarmZoneTemplate(minLevel, maxLevel, zoneTemplates, spawnPoints, onObtainMaxLevelAction, goToTownAction, availableClasses, availableTypes, availableRaces, farmMonsters, ignoredMonsters);

		for(Iterator<Element> iterator = element.elementIterator("action"); iterator.hasNext();)
		{
			Element e = iterator.next();
			int actionId = Integer.parseInt(e.attributeValue("id"));
			OrdinaryActions action = OrdinaryActions.parse(actionsHolder, e);
			actionsHolder.addAction(actionId, action);
		}

		return template;
	}
}