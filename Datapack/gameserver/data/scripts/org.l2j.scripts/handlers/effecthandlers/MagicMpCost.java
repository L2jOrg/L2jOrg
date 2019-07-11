package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.serverpackets.ExChangeMpCost;
import org.l2j.gameserver.util.MathUtil;

/**
 * @author Sdw
 */
public class MagicMpCost extends AbstractEffect {
	private final int _magicType;
	private final double _amount;
	
	public MagicMpCost(StatsSet params) {
		_magicType = params.getInt("magicType", 0);
		_amount = params.getDouble("amount", 0);
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
		effected.getStat().mergeMpConsumeTypeValue(_magicType, (_amount / 100) + 1, MathUtil::mul);
		effected.sendPacket(new ExChangeMpCost(_amount, _magicType));
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill) {
		effected.getStat().mergeMpConsumeTypeValue(_magicType, (_amount / 100) + 1, MathUtil::div);
		effected.sendPacket(new ExChangeMpCost(-_amount, _magicType));
	}
}
