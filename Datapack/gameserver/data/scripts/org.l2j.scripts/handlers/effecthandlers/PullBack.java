package handlers.effecthandlers;

import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Formulas;
import org.l2j.gameserver.network.serverpackets.FlyToLocation;
import org.l2j.gameserver.network.serverpackets.FlyToLocation.FlyType;
import org.l2j.gameserver.network.serverpackets.ValidateLocation;

import static java.util.Objects.isNull;
import static org.l2j.gameserver.util.GameUtils.isMonster;
import static org.l2j.gameserver.util.GameUtils.isPlayable;

/**
 * An effect that pulls effected target back to the effector.
 * @author Nik
 */
public final class PullBack extends AbstractEffect {

	public final int speed;
	public final int delay;
	private final int animationSpeed;
	private final FlyType type;
	
	public PullBack(StatsSet params) {
		speed = params.getInt("power", 0);
		delay = params.getInt("delay", speed);
		animationSpeed = params.getInt("animationSpeed", 0);
		type = params.getEnum("type", FlyType.class, FlyType.WARP_FORWARD); // type 9
	}
	
	@Override
	public boolean calcSuccess(Creature effector, Creature effected, Skill skill)
	{
		return Formulas.calcProbability(100, effector, effected, skill);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		// Prevent pulling raids.
		if (isNull(effected) || effected.isRaid()) {
			return;
		}

		// Prevent pulling debuff blocked characters.
		if (effected.isDebuffBlocked()) {
			return;
		}

		// Prevent pulling NPCs.
		if (!isPlayable(effected) && !isMonster(effected)) {
			return;
		}
		
		// In retail, you get debuff, but you are not even moved if there is obstacle. You are still disabled from using skills and moving though.
		if (GeoEngine.getInstance().canMoveToTarget(effected.getX(), effected.getY(), effected.getZ(), effector.getX(), effector.getY(), effector.getZ(), effector.getInstanceWorld())) {
			effected.broadcastPacket(new FlyToLocation(effected, effector, type, speed, delay, animationSpeed));
			effected.setXYZ(effector);
			effected.broadcastPacket(new ValidateLocation(effected));
			effected.revalidateZone(true);
		}
	}
}
