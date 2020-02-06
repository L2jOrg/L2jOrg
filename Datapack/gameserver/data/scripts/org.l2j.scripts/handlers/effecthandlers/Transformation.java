package handlers.effecthandlers;

import org.l2j.commons.util.Rnd;
import org.l2j.commons.util.Util;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.l2j.gameserver.util.GameUtils.isDoor;

/**
 * Transformation effect implementation.
 * @author nBd
 */
public final class Transformation extends AbstractEffect {
	public final List<Integer> id;
	
	public Transformation(StatsSet params) {
		final String ids = params.getString("transformationId", null);
		if (Util.isNotEmpty(ids)) {
			id = new ArrayList<>();
			for (String id : ids.split(";")) {
				this.id.add(Integer.parseInt(id));
			}
		} else {
			id = Collections.emptyList();
		}
	}
	
	@Override
	public boolean canStart(Creature effector, Creature effected, Skill skill)
	{
		return !isDoor(effected);
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
		if (!id.isEmpty()) {
			effected.transform(id.get(Rnd.get(id.size())), true);
		}
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill)
	{
		effected.stopTransformation(false);
	}
}
