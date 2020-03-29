package handlers.itemhandlers;

import org.l2j.gameserver.model.actor.instance.Player;

/**
 * @author JoeAlisson
 */
public class BlessedSoulShots extends SoulShots {

	@Override
	protected double getBonus(Player player) {
		return super.getBonus(player) * 2;
	}

	@Override
	protected boolean isBlessed() {
		return true;
	}
}
