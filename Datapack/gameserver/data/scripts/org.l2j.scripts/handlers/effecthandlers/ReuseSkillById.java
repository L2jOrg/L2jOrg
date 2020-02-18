package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.serverpackets.SkillCoolTime;

import static java.util.Objects.nonNull;

/**
 * @author Mobius
 * @author JoeAlisson
 */
public class ReuseSkillById extends AbstractEffect {
	private final int skillId;
	
	private ReuseSkillById(StatsSet params)
	{
		skillId = params.getInt("id", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		final Player player = effector.getActingPlayer();
		if (nonNull(player)) {
			final Skill s = player.getKnownSkill(skillId);
			if (nonNull(s)) {
				player.removeTimeStamp(s);
				player.enableSkill(s);
				player.sendPacket(new SkillCoolTime(player));
			}
		}
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new ReuseSkillById(data);
		}

		@Override
		public String effectName() {
			return "reuse-skill";
		}
	}
}
