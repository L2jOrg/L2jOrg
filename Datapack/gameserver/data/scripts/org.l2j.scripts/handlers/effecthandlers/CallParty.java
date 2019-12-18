package handlers.effecthandlers;

import org.l2j.gameserver.model.Party;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;

import static java.util.Objects.isNull;

/**
 * Call Party effect implementation.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class CallParty extends AbstractEffect {
	public CallParty(StatsSet params) {
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		final Party party = effector.getParty();

		if (isNull(party)) {
			return;
		}

		party.getMembers().stream()
				.filter(partyMember -> effector != partyMember && CallPc.checkSummonTargetStatus(partyMember, effector.getActingPlayer()))
				.forEach(partyMember -> partyMember.teleToLocation(effector, true));
	}
}
