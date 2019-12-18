package handlers.effecthandlers;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Door;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.SystemMessageId;

import static org.l2j.gameserver.util.GameUtils.isDoor;

/**
 * Open Door effect implementation.
 * @author Adry_85
 */
public final class OpenDoor extends AbstractEffect {
	private final int chance;
	private final boolean isItem;
	
	public OpenDoor(StatsSet params) {
		chance = params.getInt("chance", 0);
		isItem = params.getBoolean("isItem", false);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (!isDoor(effected) || (effector.getInstanceWorld() != effected.getInstanceWorld())) {
			return;
		}
		
		final Door door = (Door) effected;
		if ((!door.isOpenableBySkill() && !isItem) || (door.getFort() != null)) {
			effector.sendPacket(SystemMessageId.THIS_DOOR_CANNOT_BE_UNLOCKED);
			return;
		}
		
		if (Rnd.get(100) < chance && !door.isOpen()) {
			door.openMe();
		} else {
			effector.sendPacket(SystemMessageId.YOU_HAVE_FAILED_TO_UNLOCK_THE_DOOR);
		}
	}
}
