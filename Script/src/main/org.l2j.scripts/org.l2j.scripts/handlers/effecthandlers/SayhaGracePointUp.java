package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.serverpackets.UserInfo;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

public class SayhaGracePointUp  extends AbstractEffect {
    private final int _value;
    public SayhaGracePointUp(StatsSet params)
    {
        _value = params.getInt("value", 0);
    }

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
    public void instant(Creature effector, Creature effected, Skill skill, Item item)
    {
        double mul = effected.getStats().getMul(Stat.SAYHA_GRACE_POINT_MOD, 1);
        double add = effected.getStats().getAdd(Stat.SAYHA_GRACE_POINT_MOD, 0);
        effected.getActingPlayer().updateSayhaGracePoints((int) ((_value * mul) + add), false, false);
        UserInfo ui = new UserInfo(effected.getActingPlayer());
        ui.addComponentType(UserInfoType.VITA_FAME);
        effected.getActingPlayer().sendPacket(ui);
    }
    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new SayhaGracePointUp(data);
        }

        @Override
        public String effectName() {
            return "SayhaGracePointUp";
        }
    }
}
