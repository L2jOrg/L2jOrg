package l2s.gameserver.data.xml.holder;

import l2s.commons.data.xml.AbstractHolder;
import l2s.gameserver.model.base.ClassId;
import l2s.gameserver.model.base.ClassType;
import l2s.gameserver.model.base.Race;
import l2s.gameserver.model.base.Sex;
import l2s.gameserver.templates.player.PlayerTemplate;

import org.napile.primitive.maps.IntObjectMap;
import org.napile.primitive.maps.impl.HashIntObjectMap;

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