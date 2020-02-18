package handlers.effecthandlers;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.MountType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isPet;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author Sdw
 */
public class Feed extends AbstractEffect {
	private final int normal;
	private final int ride;
	private final int wyvern;
	
	private Feed(StatsSet params) {
		normal = params.getInt("normal", 0);
		ride = params.getInt("ride", 0);
		wyvern = params.getInt("wyvern", 0);
	}
	
	@Override
	public boolean isInstant()
	{
		return true;
	}
	
	@Override
	public void instant(Creature effector, Creature effected, Skill skill, Item item) {
		if (isPet(effected)) {
			final Pet pet = (Pet) effected;
			pet.setCurrentFed(pet.getCurrentFed() + (normal * Config.PET_FOOD_RATE));
		} else if (isPlayer(effected)) {
			final Player player = effected.getActingPlayer();
			if (player.getMountType() == MountType.WYVERN) {
				player.setCurrentFeed(player.getCurrentFeed() + wyvern);
			} else {
				player.setCurrentFeed(player.getCurrentFeed() + ride);
			}
		}
	}

	public static class Factory implements SkillEffectFactory {

		@Override
		public AbstractEffect create(StatsSet data) {
			return new Feed(data);
		}

		@Override
		public String effectName() {
			return "feed";
		}
	}
}
