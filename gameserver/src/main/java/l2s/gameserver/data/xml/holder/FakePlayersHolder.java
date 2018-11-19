package l2s.gameserver.data.xml.holder;

import java.util.*;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.templates.fakeplayer.FakePlayerAITemplate;
import l2s.gameserver.templates.fakeplayer.FarmZoneTemplate;
import l2s.gameserver.templates.fakeplayer.TownZoneTemplate;

import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

/**
 * @author: Bonux
 */
public final class FakePlayersHolder extends AbstractHolder
{
	private static final FakePlayersHolder _instance = new FakePlayersHolder();

	private final IntObjectMap<FakePlayerAITemplate> _aiTemplates = new HashIntObjectMap<FakePlayerAITemplate>();
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
		return _aiTemplates.values();
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
		info("loaded " + _aiTemplates.size() + " fake players ai(s) count.");
		info("loaded " + _farmZoneTemplates.size() + " fake players farm zone(s) count.");
		info("loaded " + _townZoneTemplates.size() + " fake players town zone(s) count.");
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