package handlers.itemhandlers;

import org.l2j.gameserver.model.actor.Summon;

/**
 * @author JoeAlisson
 */
public class BlessedBeastSpiritShot extends BeastSpiritShot {

	@Override
	protected boolean isBlessed() {
		return true;
	}

	@Override
	protected double getBonus(Summon summon) {
		return super.getBonus(summon) * 2;
	}
}
