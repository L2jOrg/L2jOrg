package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.effects.EffectFlag;
import org.l2j.gameserver.model.effects.EffectType;
import org.l2j.gameserver.model.items.instance.Item;

/**
 * An effect that blocks a debuff. Acts like DOTA's Linken Sphere.
 * @author Nik
 * @author JoeAlisson
 */
public final class AbnormalShield extends AbstractEffect {

    private final int power;

    private AbnormalShield(StatsSet params)
    {
        power = params.getInt("power", -1);
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item)
    {
        effected.setAbnormalShieldBlocks(power);
    }

    @Override
    public long getEffectFlags()
    {
        return EffectFlag.ABNORMAL_SHIELD.getMask();
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill)
    {
        effected.setAbnormalShieldBlocks(Integer.MIN_VALUE);
    }

    @Override
    public EffectType getEffectType()
    {
        return EffectType.ABNORMAL_SHIELD;
    }


    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new AbnormalShield(data);
        }

        @Override
        public String effectName() {
            return "AbnormalShield";
        }
    }
}