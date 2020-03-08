package handlers.itemhandlers;

import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * @author JoeAlisson
 */
public class BeastSpiritShot extends AbstractBeastShot {

	@Override
	protected ShotType getShotType(Item item) {
		var isBlessed = (item.getId() == 6647 || item.getId() == 20334); // TODO: Unhardcode
		return isBlessed ? ShotType.BLESSED_SPIRITSHOTS : ShotType.SPIRITSHOTS;
	}

	@Override
	protected void sendUsesMessage(Player player) {
		player.sendPacket(SystemMessageId.YOUR_PET_USES_SPIRITSHOT);
	}

	@Override
	protected SystemMessageId getNotEnoughMessage() {
		return SystemMessageId.YOU_DON_T_HAVE_ENOUGH_SPIRITSHOTS_NEEDED_FOR_A_SERVITOR;
	}
}
