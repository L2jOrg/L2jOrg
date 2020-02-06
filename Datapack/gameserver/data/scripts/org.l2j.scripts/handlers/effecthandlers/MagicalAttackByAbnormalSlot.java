package handlers.effecthandlers;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.enums.ShotType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.model.stats.Formulas;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Magical Attack By Abnormal Slot effect implementation.
 * @author Sdw
 */
public final class MagicalAttackByAbnormalSlot extends AbstractEffect {
	public final double power;
	public final Set<AbnormalType> abnormals;
	
	public MagicalAttackByAbnormalSlot(StatsSet params) {
		power = params.getDouble("power", 0);
		
		final String abnormals = params.getString("abnormalType", null);
		if (Util.isNotEmpty(abnormals)) {
			this.abnormals = new HashSet<>();
			for (String slot : abnormals.split(";")) {
				this.abnormals.add(AbnormalType.getAbnormalType(slot));
			}
		} else {
			this.abnormals = Collections.<AbnormalType> emptySet();
		}
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
		if (effector.isAlikeDead() || abnormals.stream().noneMatch(effected::hasAbnormalType)) {
			return;
		}
		
		if (isPlayer(effected) && effected.getActingPlayer().isFakeDeath()) {
			effected.stopFakeDeath(true);
		}
		
		final boolean sps = skill.useSpiritShot() && effector.isChargedShot(ShotType.SPIRITSHOTS);
		final boolean bss = skill.useSpiritShot() && effector.isChargedShot(ShotType.BLESSED_SPIRITSHOTS);
		final boolean mcrit = Formulas.calcCrit(skill.getMagicCriticalRate(), effector, effected, skill);
		final double damage = Formulas.calcMagicDam(effector, effected, skill, effector.getMAtk(), power, effected.getMDef(), sps, bss, mcrit);
		
		effector.doAttack(damage, effected, skill, false, false, mcrit, false);
	}
}