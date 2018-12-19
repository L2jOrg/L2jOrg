package org.l2j.gameserver.data.xml.holder;

import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;
import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.model.base.ClassType;
import org.l2j.gameserver.model.base.Race;
import org.l2j.gameserver.templates.fakeplayer.FakePlayerAITemplate;
import org.l2j.gameserver.templates.fakeplayer.FarmZoneTemplate;
import org.l2j.gameserver.templates.fakeplayer.TownZoneTemplate;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author: Bonux
 */
public final class FakePlayersHolder extends AbstractHolder
{
	private static final FakePlayersHolder _instance = new FakePlayersHolder();

	private final TIntObjectMap<FakePlayerAITemplate> _aiTemplates = new TIntObjectHashMap<>();
	private final Set<FarmZoneTemplate> _farmZoneTemplates = new HashSet<FarmZoneTemplate>();
	private final Set<TownZoneTemplate> _townZoneTemplates = new HashSet<TownZoneTemplate>();

	public static FakePlayersHolder getInstance()
	{
		return _instance;
	}

	public void addAITemplate(FakePlayerAITemplate template)
	{
		_aiTemplates.put(makeHashCode(template.getRace(), template.getType()), template);
	}

	public Collection<FakePlayerAITemplate> getAITemplates()
	{
		return _aiTemplates.valueCollection();
	}

	public FakePlayerAITemplate getAITemplate(Race race, ClassType type)
	{
		return _aiTemplates.get(makeHashCode(race, type));
	}

	public void addFarmZone(FarmZoneTemplate template)
	{
		_farmZoneTemplates.add(template);
	}

	public Collection<FarmZoneTemplate> getFarmZones()
	{
		return _farmZoneTemplates;
	}

	public void addTownZone(TownZoneTemplate template)
	{
		_townZoneTemplates.add(template);
	}

	public Collection<TownZoneTemplate> getTownZones()
	{
		return _townZoneTemplates;
	}

	private static int makeHashCode(Race race, ClassType type)
	{
		return race.ordinal() * 100000 + type.ordinal() * 1000;
	}

	@Override
	public void log()
	{
		logger.info("loaded " + _aiTemplates.size() + " fake players ai(s) count.");
		logger.info("loaded " + _farmZoneTemplates.size() + " fake players farm zone(s) count.");
		logger.info("loaded " + _townZoneTemplates.size() + " fake players town zone(s) count.");
	}

	@Deprecated
	@Override
	public int size()
	{
		return 0;
	}

	@Override
	public void clear()
	{
		_aiTemplates.clear();
		_farmZoneTemplates.clear();
		_townZoneTemplates.clear();
	}
}