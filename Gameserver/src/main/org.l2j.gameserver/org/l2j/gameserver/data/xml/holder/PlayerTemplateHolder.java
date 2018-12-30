package org.l2j.gameserver.data.xml.holder;

import org.l2j.commons.data.xml.AbstractHolder;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.model.base.ClassType;
import org.l2j.gameserver.model.base.Race;
import org.l2j.gameserver.model.base.Sex;
import org.l2j.gameserver.templates.player.PlayerTemplate;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

/**
 * @author Bonux
**/
public final class PlayerTemplateHolder extends AbstractHolder
{
	private static final PlayerTemplateHolder _instance = new PlayerTemplateHolder();
	private final IntObjectMap<PlayerTemplate> _templates = new HashIntObjectMap<PlayerTemplate>();

	public static PlayerTemplateHolder getInstance()
	{
		return _instance;
	}

	public void addPlayerTemplate(Race race, ClassType type, Sex sex, PlayerTemplate template)
	{
		_templates.put(makeHashCode(race, type, sex), template);
	}

	public PlayerTemplate getPlayerTemplate(Race race, ClassId classId, Sex sex)
	{
		race = classId.getRace();
		return _templates.get(makeHashCode(race, classId.getType(), sex));
	}

	private static int makeHashCode(Race race, ClassType type, Sex sex)
	{
		return race.ordinal() * 100000 + type.ordinal() * 1000 + sex.ordinal() * 10;
	}

	@Override
	public int size()
	{
		return _templates.size();
	}

	@Override
	public void clear()
	{
		_templates.clear();
	}
}