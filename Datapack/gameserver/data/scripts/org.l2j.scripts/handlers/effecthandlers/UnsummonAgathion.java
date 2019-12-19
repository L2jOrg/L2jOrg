package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerUnsummonAgathion;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.serverpackets.ExUserInfoCubic;

import static java.util.Objects.nonNull;

/**
 * Unsummon Agathion effect implementation.
 * @author Zoey76
 */
public final class UnsummonAgathion extends AbstractEffect {
	public UnsummonAgathion(StatsSet params) {
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
			final int agathionId = player.getAgathionId();
			if (agathionId > 0) {
				player.setAgathionId(0);
				player.sendPacket(new ExUserInfoCubic(player));
				player.broadcastCharInfo();

				EventDispatcher.getInstance().notifyEventAsync(new OnPlayerUnsummonAgathion(player, agathionId));
			}
		}
	}
}
