package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Change Hair Style effect implementation.
 * @author Zoey76
 */
public final class ChangeHairStyle extends AbstractEffect {
	public final int value;
	
	public ChangeHairStyle(StatsSet params)
	{
		value = params.getInt("value", 0);
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
		player.getAppearance().setHairStyle(value);
		player.broadcastUserInfo();
	}
}
