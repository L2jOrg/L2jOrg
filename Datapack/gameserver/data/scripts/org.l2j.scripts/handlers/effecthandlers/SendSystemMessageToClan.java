package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author Nik
 * @author JoeAlisson
 */
public class SendSystemMessageToClan extends AbstractEffect {

	private final SystemMessage message;
	
	private SendSystemMessageToClan(StatsSet params) {
		final int id = params.getInt("id", 0);
		message = SystemMessage.getSystemMessage(id);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		final Player player = effected.getActingPlayer();
		if (isNull(player) || (message == null)) {
			return;
		}
		
		final Clan clan = player.getClan();
		if (nonNull(clan)) {
			clan.broadcastToOnlineMembers(message);
		}
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new SendSystemMessageToClan(data);
		}

		@Override
		public String effectName() {
			return "clan-system-message";
		}
	}
}
