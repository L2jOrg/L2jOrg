package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.serverpackets.SkillCoolTime;

import static java.util.Objects.nonNull;

/**
 * @author Mobius
 */
public class ReuseSkillById extends AbstractEffect {
	private final int skillId;
	
	public ReuseSkillById(StatsSet params)
	{
		skillId = params.getInt("skillId", 0);
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
}
