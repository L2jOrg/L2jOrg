package handlers.effecthandlers;

import io.github.joealisson.primitive.Containers;
import io.github.joealisson.primitive.HashIntSet;
import io.github.joealisson.primitive.IntSet;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.instancezone.Instance;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isPet;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Resurrection Special effect implementation.
 * @author Zealar
 * @author JoeAlisson
 */
public final class ResurrectionSpecial extends AbstractEffect {
	private final int power;
	private final IntSet instanceIds;
	
	private ResurrectionSpecial(StatsSet params) {
		power = params.getInt("power", 0);
		
		final String instanceIds = params.getString("instanceId", null);
		if ((instanceIds != null) && !instanceIds.isEmpty()) {
			this.instanceIds = new HashIntSet();
			for (String id : instanceIds.split(";")) {
				this.instanceIds.add(Integer.parseInt(id));
			}
		} else {
			this.instanceIds = Containers.emptyIntSet();
		}
	}
	
	@Override
	public EffectType getEffectType()
	{
		return EffectType.RESURRECTION_SPECIAL;
	}
	
	@Override
	public long getEffectFlags()
	{
		return EffectFlag.RESURRECTION_SPECIAL.getMask();
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill) {
		if (!isPlayer(effected) && !isPet(effected)) {
			return;
		}
		
		final Player caster = effector.getActingPlayer();
		final Instance instance = caster.getInstanceWorld();
		if (!instanceIds.isEmpty() && ((instance == null) || !instanceIds.contains(instance.getTemplateId()))) {
			return;
		}
		
		if (isPlayer(effected)) {
			effected.getActingPlayer().reviveRequest(caster, skill, false, power);
		} else if (isPet(effected)) {
			final Pet pet = (Pet) effected;
			effected.getActingPlayer().reviveRequest(pet.getActingPlayer(), skill, true, power);
		}
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new ResurrectionSpecial(data);
		}

		@Override
		public String effectName() {
			return "ResurrectionSpecial";
		}
	}
}