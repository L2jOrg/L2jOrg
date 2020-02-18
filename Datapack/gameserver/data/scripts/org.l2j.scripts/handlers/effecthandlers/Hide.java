package handlers.effecthandlers;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Hide effect implementation.
 * @author ZaKaX, nBd
 * @author JoeAlisson
 */
public final class Hide extends AbstractEffect {

	private Hide() {
	}
	
	@Override
	public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
		if (isPlayer(effected)) {
			effected.setInvisible(true);

			if ((effected.getAI().getNextIntention() != null) && (effected.getAI().getNextIntention().getCtrlIntention() == CtrlIntention.AI_INTENTION_ATTACK)) {
				effected.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			}

			World.getInstance().forEachVisibleObject(effected, Creature.class, target -> {
				target.setTarget(null);
				target.abortAttack();
				target.abortCast();
				target.getAI().setIntention(CtrlIntention.AI_INTENTION_IDLE);
			}, target -> target.getTarget() == effected);
		}
	}
	
	@Override
	public void onExit(Creature effector, Creature effected, Skill skill) {
		if (isPlayer(effected)) {
			final Player activeChar = effected.getActingPlayer();
			if (!activeChar.inObserverMode()) {
				activeChar.setInvisible(false);
			}
		}
	}

	public static class Factory implements SkillEffectFactory {
		private static final Hide INSTANCE = new Hide();
		@Override
		public AbstractEffect create(StatsSet data) {
			return INSTANCE;
		}

		@Override
		public String effectName() {
			return "Hide";
		}
	}
}