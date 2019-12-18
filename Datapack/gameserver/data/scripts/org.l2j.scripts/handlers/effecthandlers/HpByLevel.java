package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;

/**
 * Hp By Level effect implementation.
 * @author Zoey76
 */
public final class HpByLevel extends AbstractEffect {
	private final double power;
	
	public HpByLevel(StatsSet params)
	{
		power = params.getDouble("power", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		final double abs = power;
		final double absorb = effector.getCurrentHp() + abs > effector.getMaxHp() ? effector.getMaxHp() : effector.getCurrentHp() + abs;
		final int restored = (int) (absorb - effector.getCurrentHp());
		effector.setCurrentHp(absorb);
		effector.sendPacket(getSystemMessage(SystemMessageId.S1_HP_HAS_BEEN_RESTORED).addInt(restored));
	}
}
