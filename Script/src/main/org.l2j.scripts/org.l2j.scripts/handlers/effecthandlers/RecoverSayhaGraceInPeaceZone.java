package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.stat.PlayerStats;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.skills.BuffInfo;
import org.l2j.gameserver.world.zone.ZoneType;

import java.time.ZoneId;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

public class RecoverSayhaGraceInPeaceZone extends AbstractEffect {
    private final double _amount;
    private final int _ticks;

    public RecoverSayhaGraceInPeaceZone(StatsSet params)
    {
        _amount = params.getDouble("amount", 0);
        _ticks = params.getInt("ticks", 10);
    }

    @Override
    public int getTicks()
    {
        return _ticks;
    }

    @Override
    public boolean onActionTime(Creature effector, Creature effected, Skill skill, Item item)
    {
        if ((effected == null) //
                || effected.isDead() //
                || !isPlayer(effected) //
                || !effected.isInsideZone(ZoneType.PEACE))
        {
            return false;
        }

        long vitality = effected.getActingPlayer().getSayhaGracePoints();
        vitality += _amount;
        if (vitality >= PlayerStats.MAX_SAYHA_GRACE_POINTS)
        {
            vitality = PlayerStats.MAX_SAYHA_GRACE_POINTS;
        }
        effected.getActingPlayer().setSayhaGracePoints((int) vitality, true);

        return skill.isToggle();
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill)
    {
        if ((effected != null) //
                && isPlayer(effected))
        {
            final BuffInfo info = effected.getEffectList().getBuffInfoBySkillId(skill.getId());
            if ((info != null) && !info.isInUse())
            {
                long vitality = effected.getActingPlayer().getSayhaGracePoints();
                vitality += _amount * 100;
                if (vitality >= PlayerStats.MAX_SAYHA_GRACE_POINTS)
                {
                    vitality = PlayerStats.MAX_SAYHA_GRACE_POINTS;
                }
                effected.getActingPlayer().setSayhaGracePoints((int) vitality, true);
            }
        }
    }
    public static class Factory implements SkillEffectFactory {
        @Override
        public AbstractEffect create(StatsSet data) {
            return new RecoverSayhaGraceInPeaceZone(data);
        }

        @Override
        public String effectName() {
            return "RecoverSayhaGraceInPeaceZone";
        }
    }
}
