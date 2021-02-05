package org.l2j.scripts.handlers.effecthandlers;

import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.enums.UserInfoType;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.network.serverpackets.UserInfo;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

public class Elixir extends AbstractEffect {

    @Override
    public boolean isInstant() {
        return true;
    }

    @Override
    public boolean canStart(Creature effector, Creature effected, Skill skill) {

        return super.canStart(effector, effected, skill);
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (isPlayer(effected)) {
           var player = effector.getActingPlayer();
            player.setElixirsPoints((short) 1);
            player.sendPacket(new UserInfo(player, UserInfoType.ELIXIR_USED, UserInfoType.STATS_POINTS, UserInfoType.STATS_ABILITIES));
        }
    }

    @Override
    public EffectType getEffectType() {
        return EffectType.ELIXIR;
    }

    public static class Factory implements SkillEffectFactory {
        private static final Elixir INSTANCE = new Elixir();

        @Override
        public AbstractEffect create(StatsSet data) {
            return INSTANCE;
        }

        @Override
        public String effectName() {
            return "Elixir";
        }
    }
}