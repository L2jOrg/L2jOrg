package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.data.xml.MagicLampData;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;

public class ModifyMagicLampPoints extends AbstractEffect {
    private final int _amount;

    private ModifyMagicLampPoints(StatsSet params)
    {
        _amount = params.getInt("amount");
    }

    @Override
    public boolean isInstant()
    {
        return true;
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (effected == null)
        {
            return;
        }

        final Player player = effected.getActingPlayer();
        if (player == null)
        {
            return;
        }

        MagicLampData.getInstance().addLampExp(player, _amount, false);
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new ModifyMagicLampPoints(data);
        }

        @Override
        public String effectName() {
            return "ModifyMagicLampPoints";
        }
    }
}
