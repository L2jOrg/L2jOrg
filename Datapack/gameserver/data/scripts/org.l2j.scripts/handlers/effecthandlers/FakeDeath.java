package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ChangeWaitType;
import org.l2j.gameserver.network.serverpackets.Revive;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Fake Death effect implementation.
 * @author mkizub
 * @author JoeAlisson
 */
public final class FakeDeath extends AbstractEffect {
	private final double power;
	
	private FakeDeath(StatsSet params) {
		power = params.getDouble("power", 0);
		setTicks(params.getInt("ticks"));
	}
	
	@Override
	public long getEffectFlags()
	{
		return EffectFlag.FAKE_DEATH.getMask();
	}
	
	@Override
	public boolean onActionTime(Creature effector, Creature effected, Skill skill, Item item) {
		if (effected.isDead()) {
			return false;
		}
		
		final double manaDam = power * getTicksMultiplier();
		if (manaDam > effected.getCurrentMp()) {
			if (skill.isToggle()) {
				effected.sendPacket(SystemMessageId.YOUR_SKILL_WAS_DEACTIVATED_DUE_TO_LACK_OF_MP);
				return false;
			}
		}
		
		effected.reduceCurrentMp(manaDam);
		return skill.isToggle();
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill) {
		if (isPlayer(effected)) {
			effected.getActingPlayer().setRecentFakeDeath(true);
		}
		
		effected.broadcastPacket(new ChangeWaitType(effected, ChangeWaitType.WT_STOP_FAKEDEATH));
		effected.broadcastPacket(new Revive(effected));
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item)
	{
		effected.startFakeDeath();
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new FakeDeath(data);
		}

		@Override
		public String effectName() {
			return "FakeDeath";
		}
	}
}
