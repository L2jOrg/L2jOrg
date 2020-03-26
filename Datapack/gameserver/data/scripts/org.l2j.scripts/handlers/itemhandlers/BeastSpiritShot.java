package handlers.itemhandlers;

import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * @author JoeAlisson
 */
public class BeastSpiritShot extends AbstractBeastShot {

	@Override
	protected ShotType getShotType() {
		return ShotType.SPIRITSHOTS;
	}

	@Override
	protected boolean isBlessed() {
		return false;
	}

	@Override
	protected double getBonus(Summon summon) {
		return summon.getStats().getValue(Stat.SPIRIT_SHOTS_BONUS, 1) * 2;
	}

	@Override
	protected void sendUsesMessage(Player player) {
		player.sendPacket(SystemMessageId.YOUR_PET_USES_SPIRITSHOT);
	}

}
