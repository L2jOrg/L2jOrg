package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Change Face effect implementation.
 * @author Zoey76
 */
public final class ChangeFace extends AbstractEffect {
	public final int power;
	
	public ChangeFace(StatsSet params)
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
		if (!isPlayer(effected)) {
			return;
		}
		
		final Player player = effected.getActingPlayer();
		player.getAppearance().setFace(power);
		player.broadcastUserInfo();
	}
}
