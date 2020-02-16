package handlers.targethandlers.affectscope;

import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.skills.targets.AffectScope;

import java.util.function.Consumer;

/**
 * Square affect scope implementation (actually more like a rectangle).
 * @author Nik
 */
public class Square extends SquarePB {

	@Override
	public void forEachAffected(Creature activeChar, WorldObject target, Skill skill, Consumer<? super WorldObject> action) {
		final int squareLength = skill.getFanRadius();
		final int squareWidth = skill.getFanAngle();
		final int radius = (int) Math.sqrt((squareLength * squareLength) + (squareWidth * squareWidth));
		var filter = squareFilterOf(activeChar, skill, squareLength, squareWidth);

		World.getInstance().forEachVisibleObjectInRange(activeChar, Creature.class, radius, action::accept, filter);

		// Add object of origin since its skipped in the forEachVisibleObjectInRange method.
		if (filter.test(activeChar)) {
			action.accept(activeChar);
		}

	}
	
	@Override
	public Enum<AffectScope> getAffectScopeType()
	{
		return AffectScope.SQUARE;
	}
}
