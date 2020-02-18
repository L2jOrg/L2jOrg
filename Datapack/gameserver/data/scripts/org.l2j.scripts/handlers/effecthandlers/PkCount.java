package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.serverpackets.UserInfo;

import static java.util.Objects.isNull;

/**
 * Item Effect: Increase/decrease PK count permanently.
 * @author Nik
 * @author JoeAlisson
 */
public class PkCount extends AbstractEffect {
	private final int power;
	
	private PkCount(StatsSet params)
	{
		power = params.getInt("power", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		final Player player = effected.getActingPlayer();

		if (isNull(player)) {
			return;
		}
		
		if (player.getPkKills() > 0) {
			final int newPkCount = Math.max(player.getPkKills() + power, 0);
			player.setPkKills(newPkCount);
			player.sendPacket(new UserInfo(player));
		}
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new PkCount(data);
		}

		@Override
		public String effectName() {
			return "PkCount";
		}
	}
}
