package org.l2j.scripts.ai.locations.crumatower;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.ai.Fighter;
import org.l2j.gameserver.data.xml.holder.SkillHolder;
import org.l2j.gameserver.geodata.GeoEngine;
import org.l2j.gameserver.model.Creature;
import org.l2j.gameserver.model.Skill;
import org.l2j.gameserver.model.instances.NpcInstance;
import org.l2j.gameserver.network.l2.s2c.FlyToLocationPacket;
import org.l2j.gameserver.utils.Location;
import org.l2j.gameserver.utils.PositionUtils;

/**
 * @author Bonux
**/
public class Perum extends Fighter
{
	private static final Skill SUMMON_PC = SkillHolder.getInstance().getSkill(4161, 1);

	public Perum(NpcInstance actor)
	{
		super(actor);
	}

	@Override
	protected void onEvtAttacked(Creature attacker, Skill skill, int damage)
	{
		super.onEvtAttacked(attacker, skill, damage);

		NpcInstance actor = getActor();
		if(actor.isDead() || actor.isAMuted())
			return;

		final double distance = actor.getDistance(attacker);
		final int chance = Rnd.get(100);

		if(distance > 300)
		{
			if(chance < 50)
				addTaskCast(attacker, SUMMON_PC);
		}
		else if(distance > 100)
		{
			if(actor.getAggroList().getMostHated(getMaxHateRange()) == attacker && chance < 50 || chance < 10)
				addTaskCast(attacker, SUMMON_PC);
		}
	}
	
	@Override
	protected void onEvtFinishCasting(Skill skill, Creature target, boolean success)
	{
		if(!success)
			return;

		if(skill == SUMMON_PC)
		{
			NpcInstance actor = getActor();
			if(actor.isDead())
				return;

			if(target == null || target.isDead())
				return;

			target.abortAttack(true, true);
			target.abortCast(true, true);
			target.stopMove();

			double radian = PositionUtils.convertHeadingToDegree(actor.getHeading());
			if(radian > 360)
				radian -= 360;

			radian = (Math.PI * radian) / 180;

			Location loc = new Location(actor.getX() + (int) (Math.cos(radian) * 40), actor.getY() + (int) (Math.sin(radian) * 40), actor.getZ());
			loc.correctGeoZ();

			if(!GeoEngine.canMoveToCoord(target.getX(), target.getY(), target.getZ(), loc.x, loc.y, loc.z, actor.getGeoIndex()))
			{
				loc = target.getLoc(); // Если не получается встать рядом с объектом, пробуем встать прямо в него
				if(!GeoEngine.canMoveToCoord(target.getX(), target.getY(), target.getZ(), loc.x, loc.y, loc.z, actor.getGeoIndex()))
					return;
			}
			target.broadcastPacket(new FlyToLocationPacket(target, loc, FlyToLocationPacket.FlyType.DUMMY, 0, 0, 0));
			target.setLoc(loc);
			target.validateLocation(1);
		}
		super.onEvtFinishCasting(skill, target, success);
	}
}