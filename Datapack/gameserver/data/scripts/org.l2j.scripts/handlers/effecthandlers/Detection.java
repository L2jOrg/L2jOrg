package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Detection effect implementation.
 * @author UnAfraid
 */
public final class Detection extends AbstractEffect {
	public Detection(StatsSet params) {
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (!isPlayer(effector) || !isPlayer(effected)) {
			return;
		}
		
		final Player player = effector.getActingPlayer();
		final Player target = effected.getActingPlayer();
		final boolean hasParty = player.isInParty();
		final boolean hasClan = player.getClanId() > 0;
		final boolean hasAlly = player.getAllyId() > 0;
		
		if (target.isInvisible()) {
			if (hasParty && (target.isInParty()) && (player.getParty().getLeaderObjectId() == target.getParty().getLeaderObjectId())) {
				return;
			}
			else if (hasClan && (player.getClanId() == target.getClanId())) {
				return;
			} else if (hasAlly && (player.getAllyId() == target.getAllyId())) {
				return;
			}
			
			// Remove Hide.
			target.getEffectList().stopEffects(AbnormalType.HIDE);
		}
	}
}
