package handlers.itemhandlers;

import org.l2j.gameserver.enums.ShotType;

/**
 * @author JoeAlisson
 */
public class BlessedSoulShots extends SoulShots {

	@Override
	protected ShotType getShotType() {
		return ShotType.BLESSED_SOULSHOTS;
	}
}
