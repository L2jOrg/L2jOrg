package handlers.effecthandlers;

import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import static org.l2j.gameserver.network.serverpackets.SystemMessage.getSystemMessage;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Magical Attack MP effect.
 * @author Adry_85
 */
public final class MagicalAttackMp extends AbstractEffect {
	private final double power;
	private final boolean critical;
	private final double criticalLimit;
	
	public MagicalAttackMp(StatsSet params) {
		power = params.getDouble("power");
		critical = params.getBoolean("critical");
		criticalLimit = params.getDouble("criticalLimit");
	}
	
	@Override
	public boolean calcSuccess(Creature effector, Creature effected, Skill skill) {
		if (effected.isMpBlocked()) {
			return false;
		}
		
		if (isPlayer(effector) && isPlayer(effected) && effected.isAffected(EffectFlag.DUELIST_FURY) && !effector.isAffected(EffectFlag.DUELIST_FURY)) {
			return false;
		}
		
		if (!Formulas.calcMagicAffected(effector, effected, skill)) {
			if (isPlayer(effector)) {
				effector.sendPacket(SystemMessageId.YOUR_ATTACK_HAS_FAILED);
			}
			if (isPlayer(effected)) {
				effected.sendPacket( getSystemMessage(SystemMessageId.C1_RESISTED_C2_S_DRAIN).addString(effected.getName()).addString(effector.getName()));
			}
			return false;
		}
		return true;
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.MAGICAL_ATTACK;
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (effector.isAlikeDead()) {
			return;
		}
		
		final boolean sps = skill.useSpiritShot() && effector.isChargedShot(ShotType.SPIRITSHOTS);
		final boolean bss = skill.useSpiritShot() && effector.isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
		final byte shld = Formulas.calcShldUse(effector, effected);
		final boolean mcrit = critical && Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill);
		final double damage = Formulas.calcManaDam(effector, effected, skill, power, shld, sps, bss, mcrit, criticalLimit);
		final double mp = Math.min(effected.getCurrentMp(), damage);
		
		if (damage > 0) {
			effected.stopEffectsOnDamage();
			effected.setCurrentMp(effected.getCurrentMp() - mp);
		}
		
		if (isPlayer(effected)) {
			effected.sendPacket(getSystemMessage(SystemMessageId.S2_S_MP_HAS_BEEN_DRAINED_BY_C1).addString(effector.getName()).addInt((int) mp));
		}
		
		if (isPlayer(effector)) {
			effector.sendPacket( getSystemMessage(SystemMessageId.YOUR_OPPONENT_S_MP_WAS_REDUCED_BY_S1).addInt((int) mp));
		}
	}
}
