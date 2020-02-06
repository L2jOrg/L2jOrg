package handlers.effecthandlers;

import org.l2j.gameserver.enums.StatModifierType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import static java.util.Objects.nonNull;
import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.isDoor;

/**
 * HP change effect. It is mostly used for potions and static damage.
 * @author Nik
 * @author JoeAlisson
 */
public final class Hp extends AbstractEffect {
	public final int amount;
	public final StatModifierType mode;
	
	public Hp(StatsSet params) {
		amount = params.getInt("amount", 0);
		mode = params.getEnum("mode", StatModifierType.class, StatModifierType.DIFF);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (effected.isDead() || isDoor(effected) || effected.isHpBlocked()) {
			return;
		}

		int basicAmount = amount;
		if (nonNull(item) && (item.isPotion() || item.isElixir())) {
			basicAmount += effected.getStats().getValue(Stat.ADDITIONAL_POTION_HP, 0);
		}
		
		double amount = 0;
		switch (mode) {
			case DIFF -> amount = Math.min(basicAmount, effected.getMaxRecoverableHp() - effected.getCurrentHp());
			case PER -> amount = Math.min((effected.getMaxHp() * basicAmount) / 100.0, effected.getMaxRecoverableHp() - effected.getCurrentHp());
		}
		
		if (amount >= 0) {
			if (amount != 0) {
				effected.setCurrentHp(amount + effected.getCurrentHp(), false);
				effected.broadcastStatusUpdate(effector);
			}
			
			SystemMessage sm;
			if (effector.getObjectId() != effected.getObjectId()) {
				sm = getSystemMessage(SystemMessageId.S2_HP_HAS_BEEN_RESTORED_BY_C1).addString(effector.getName());
			} else {
				sm = getSystemMessage(SystemMessageId.S1_HP_HAS_BEEN_RESTORED);
			}
			sm.addInt((int) amount);
			effected.sendPacket(sm);
		} else {
			final double damage = -amount;
			effected.reduceCurrentHp(damage, effector, skill, false, false, false, false);
			effector.sendDamageMessage(effected, skill, (int) damage, 0, false, false);
		}
	}
}
