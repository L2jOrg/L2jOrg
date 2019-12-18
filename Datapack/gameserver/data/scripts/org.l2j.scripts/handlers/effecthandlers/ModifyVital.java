package handlers.effecthandlers;

import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.Skill;

import static java.lang.Math.max;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Modify vital effect implementation.
 * @author malyelfik
 */
public final class ModifyVital extends AbstractEffect {

	private final ModifyType type;
	private final int hp;
	private final int mp;
	private final int cp;
	
	public ModifyVital(StatsSet params) {
		type = params.getEnum("type", ModifyType.class);
		if (type != ModifyType.SET) {
			hp = params.getInt("hp", 0);
			mp = params.getInt("mp", 0);
			cp = params.getInt("cp", 0);
		} else {
			hp = params.getInt("hp", -1);
			mp = params.getInt("mp", -1);
			cp = params.getInt("cp", -1);
		}
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

		switch (type) {
			case DIFF -> {
				effected.setCurrentCp(effected.getCurrentCp() + cp);
				effected.setCurrentHp(effected.getCurrentHp() + hp);
				effected.setCurrentMp(effected.getCurrentMp() + mp);
			}
			case SET -> {
				effected.setCurrentCp(max(cp, 0));
				effected.setCurrentHp(max(hp, 0));
				effected.setCurrentMp(max(mp, 0));
			}
			case PER -> {
				effected.setCurrentCp(effected.getCurrentCp() + (effected.getMaxCp() * (cp / 100.)));
				effected.setCurrentHp(effected.getCurrentHp() + (effected.getMaxHp() * (hp / 100.)));
				effected.setCurrentMp(effected.getCurrentMp() + (effected.getMaxMp() * (mp / 100.)));
			}
		}
	}

	private enum ModifyType {
		DIFF,
		SET,
		PER
	}

}
