package handlers.targethandlers.affectscope;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.targets.AffectScope;
import org.l2j.gameserver.world.World;

import java.util.function.Consumer;

/**
 * Fan affect scope implementation. Gathers objects in a certain angle of circular area around yourself (including origin itself).
 * @author Nik
 * @author JoeAlisson
 */
public class Fan extends FanPB {

	@Override
	public void forEachAffected(Creature activeChar, WorldObject target, Skill skill, Consumer<? super WorldObject> action) {
		var filter = fanFilterOf(activeChar, skill);
		World.getInstance().forEachVisibleObjectInRange(activeChar, Creature.class, skill.getFanRadius(), action::accept, filter);

		// Add object of origin since its skipped in the forEachVisibleObjectInRange method.
		if (filter.test(activeChar)) {
			action.accept(activeChar);
		}
	}
	
	@Override
	public Enum<AffectScope> getAffectScopeType()
	{
		return AffectScope.FAN;
	}
}
