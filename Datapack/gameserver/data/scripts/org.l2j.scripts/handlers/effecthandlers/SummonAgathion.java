package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerSummonAgathion;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.serverpackets.ExUserInfoCubic;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Summon Agathion effect implementation.
 * @author Zoey76
 */
public final class SummonAgathion extends AbstractEffect {
	private final int npcId;
	
	private SummonAgathion(StatsSet params) {
		if (params.isEmpty()) {
			LOGGER.warn("must have parameters.");
		}
		
		npcId = params.getInt("id", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (!isPlayer(effected)) {
			return;
		}
		
		final Player player = effected.getActingPlayer();
		
		player.setAgathionId(npcId);
		player.sendPacket(new ExUserInfoCubic(player));
		player.broadcastCharInfo();

		EventDispatcher.getInstance().notifyEventAsync(new OnPlayerSummonAgathion(player, npcId));
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new SummonAgathion(data);
		}

		@Override
		public String effectName() {
			return "summon-agathion";
		}
	}
}
