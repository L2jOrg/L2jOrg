package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import static java.lang.Math.max;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Modify vital effect implementation.
 * @author malyelfik
 * @author JoeAlisson
 */
public final class ModifyVital extends AbstractEffect {

	public final int hp;
	public final int mp;
	public final int cp;
	
	public ModifyVital(StatsSet params) {
		hp = params.getInt("hp", 0);
		mp = params.getInt("mp", 0);
		cp = params.getInt("cp", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (effected.isDead()) {
			return;
		}

		if (isPlayer(effector) && isPlayer(effected) && effected.isAffected(EffectFlag.DUELIST_FURY) && !effector.isAffected(EffectFlag.DUELIST_FURY)) {
			return;
		}

		effected.setCurrentCp(max(cp, 0));
		effected.setCurrentHp(max(hp, 0));
		effected.setCurrentMp(max(mp, 0));
	}
}
