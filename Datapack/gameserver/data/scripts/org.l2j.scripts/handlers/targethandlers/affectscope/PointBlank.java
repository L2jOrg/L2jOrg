package handlers.targethandlers.affectscope;

import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.handler.AffectObjectHandler;
import org.l2j.gameserver.handler.IAffectObjectHandler;
import org.l2j.gameserver.handler.IAffectScopeHandler;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.targets.AffectObject;
import org.l2j.gameserver.model.skills.targets.AffectScope;
import org.l2j.gameserver.model.skills.targets.TargetType;
import org.l2j.gameserver.world.World;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static org.l2j.gameserver.util.GameUtils.isPlayable;
import static org.l2j.gameserver.util.MathUtil.calculateDistance2D;
import static org.l2j.gameserver.util.MathUtil.isInsideRadius3D;

/**
 * Point Blank affect scope implementation. Gathers targets in specific radius except initial target.
 * @author Nik
 */
public class PointBlank implements IAffectScopeHandler
{
	@Override
	public void forEachAffected(Creature activeChar, WorldObject target, Skill skill, Consumer<? super WorldObject> action)
	{
		final IAffectObjectHandler affectObject = AffectObjectHandler.getInstance().getHandler(skill.getAffectObject());
		final int affectRange = skill.getAffectRange();
		final int affectLimit = skill.getAffectLimit();
		
		// Target checks.
		final AtomicInteger affected = new AtomicInteger(0);
		final Predicate<Creature> filter = c ->
		{
			if ((affectLimit > 0) && (affected.get() >= affectLimit))
			{
				return false;
			}
			if (affectObject != null)
			{
				if (c.isDead() && (skill.getAffectObject() != AffectObject.OBJECT_DEAD_NPC_BODY))
				{
					return false;
				}
				if (!affectObject.checkAffectedObject(activeChar, c))
				{
					return false;
				}
			}
			if (!GeoEngine.getInstance().canSeeTarget(target, c))
			{
				return false;
			}
			
			affected.incrementAndGet();
			return true;
		};
		
		// Check and add targets.
		if (skill.getTargetType() == TargetType.GROUND)
		{
			if (isPlayable(activeChar))
			{
				final Location worldPosition = activeChar.getActingPlayer().getCurrentSkillWorldPosition();
				if (worldPosition != null)
				{
					World.getInstance().forEachVisibleObjectInRange(activeChar, Creature.class, (int) (affectRange + calculateDistance2D(activeChar, worldPosition)), c ->
					{
						if (!isInsideRadius3D(c, worldPosition, affectRange))
						{
							return;
						}
						if (filter.test(c))
						{
							action.accept(c);
						}
					});
				}
			}
		} else {
			World.getInstance().forEachVisibleObjectInRange(target, Creature.class, affectRange, c -> {
				if (filter.test(c)) {
					action.accept(c);
				}
			});
		}
	}
	
	@Override
	public Enum<AffectScope> getAffectScopeType()
	{
		return AffectScope.POINT_BLANK;
	}
}
