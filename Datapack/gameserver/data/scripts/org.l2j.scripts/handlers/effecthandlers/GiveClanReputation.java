package handlers.effecthandlers;

import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.SystemMessageId;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Give Clan reputation effect implementation.
 * @author Mobius
 */
public final class GiveClanReputation extends AbstractEffect {
	public final int reputation;
	
	public GiveClanReputation(StatsSet params)
	{
		reputation = params.getInt("reputation", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		Clan clan;
		if (!isPlayer(effector) || !isPlayer(effected) || effected.isAlikeDead() || isNull(clan = effector.getClan())) {
			return;
		}
		
		clan.addReputationScore(reputation, true);
		clan.broadcastToOnlineMembers(getSystemMessage(SystemMessageId.YOUR_CLAN_HAS_ADDED_S1_POINT_S_TO_ITS_CLAN_REPUTATION).addInt(reputation));
	}
}