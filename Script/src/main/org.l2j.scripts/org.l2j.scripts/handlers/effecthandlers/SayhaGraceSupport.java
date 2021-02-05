package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.commons.util.Rnd;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.scripts.handlers.targethandlers.Item;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

public class SayhaGraceSupport  extends AbstractEffect
{
       @Override
    public boolean canStart(Creature effector, Creature effected, Skill skill)
    {

        return isPlayer(effected);
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, org.l2j.gameserver.engine.item.Item item) {
        final Player player = effected.getActingPlayer();
        final double rnd = Rnd.nextDouble() * 100;
        if (rnd <= 0.1) // 4h
        {
            player.setSayhaGraceSupportEndTime(System.currentTimeMillis() + (3600000 * 4));
        }
        else if (rnd <= 0.3) // 3h
        {
            player.setSayhaGraceSupportEndTime(System.currentTimeMillis() + (3600000 * 3));
        }
        else if (rnd <= 0.6) // 2h
        {
            player.setSayhaGraceSupportEndTime(System.currentTimeMillis() + (3600000 * 2));
        }
        else if (rnd <= 1.1) // 1h
        {
            player.setSayhaGraceSupportEndTime(System.currentTimeMillis() + (3600000 * 1));
        }
        super.instant(effector, effected, skill, item);
    }
    public static class Factory implements SkillEffectFactory {

        private static final SayhaGraceSupport INSTANCE = new SayhaGraceSupport();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "SayaGraceSupport";
        }
    }
}

