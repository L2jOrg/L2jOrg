package handlers.effecthandlers;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Chest;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;

/**
 * Open Chest effect implementation.
 * @author Adry_85
 * @author JoeAlisson
 */
public final class OpenChest extends AbstractEffect {

    private OpenChest() {
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (!(effected instanceof Chest)) {
            return;
        }

        final Player player = effector.getActingPlayer();
        final Chest chest = (Chest) effected;

        if (chest.isDead() || (player.getInstanceWorld() != chest.getInstanceWorld())) {
            return;
        }

        if (((player.getLevel() <= 77) && (Math.abs(chest.getLevel() - player.getLevel()) <= 6)) || ((player.getLevel() >= 78) && (Math.abs(chest.getLevel() - player.getLevel()) <= 5))) {
            player.broadcastSocialAction(3);
            chest.setSpecialDrop();
            chest.setMustRewardExpSp(false);
            chest.reduceCurrentHp(chest.getMaxHp(), player, skill);
        } else {
            player.broadcastSocialAction(13);
            chest.addDamageHate(player, 0, 1);
            chest.getAI().setIntention(CtrlIntention.AI_INTENTION_ATTACK, player);
        }
    }

    public static class Factory implements SkillEffectFactory {

        private static final OpenChest INSTANCE = new OpenChest();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "OpenChest";
        }
    }
}
