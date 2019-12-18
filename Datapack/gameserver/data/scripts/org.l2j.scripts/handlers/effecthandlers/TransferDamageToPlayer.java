package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Stat;

import static org.l2j.gameserver.util.GameUtils.isPlayable;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Transfer Damage effect implementation.
 * @author UnAfraid
 */
public final class TransferDamageToPlayer extends AbstractStatAddEffect {
	public TransferDamageToPlayer(StatsSet params)
	{
		super(params, Stat.TRANSFER_DAMAGE_TO_PLAYER);
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill) {
		if (isPlayable(effected) && isPlayer(effector)) {
			((Playable) effected).setTransferDamageTo(null);
		}
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
		if (isPlayable(effected) && isPlayer(effector)) {
			((Playable) effected).setTransferDamageTo(effector.getActingPlayer());
		}
	}
}