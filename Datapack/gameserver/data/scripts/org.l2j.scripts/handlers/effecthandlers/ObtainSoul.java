package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.network.serverpackets.EtcStatusUpdate;
import org.l2j.gameserver.network.serverpackets.ExSpawnEmitter;
import org.l2j.gameserver.network.serverpackets.ExSpawnEmitter.SpawnEmitterType;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author JoeAlisson
 */
public class ObtainSoul extends AbstractEffect {
    private final int power;
    private final boolean isShine;

    private ObtainSoul(StatsSet data) {
        power = data.getInt("power");
        isShine = data.getBoolean("is-shine");
    }

    @Override
    public void instant(Creature effector, Creature effected, Skill skill, Item item) {
        if (!isPlayer(effected) || effected.isAlikeDead()) {
            return;
        }

        if(effected.hasAbnormalType(AbnormalType.KAMAEL_SPECIAL)) {
            return;
        }

        final var player = effected.getActingPlayer();
        if(isShine) {
            var souls = player.getShineSouls();
            if(souls + power >= 100) {
                player.setShineSouls((byte) 0);
                var level = player.getSkillLevel(CommonSkill.SHINE_MASTERY.getId());
                SkillCaster.triggerCast(player, player, SkillEngine.getInstance().getSkill(CommonSkill.SHINE_SIDE.getId(), level));
            } else {
                player.setShineSouls((byte) (souls + power));
            }
        } else {
            var souls = player.getShadowSouls();
            if(souls + power >= 100) {
                player.setShadowSouls((byte) 0);
                var level = player.getSkillLevel(CommonSkill.SHADOW_MASTERY.getId());
                SkillCaster.triggerCast(player, player, SkillEngine.getInstance().getSkill(CommonSkill.SHADOW_SIDE.getId(), level));
            } else {
                player.setShadowSouls((byte) (souls + power));
            }
        }
        player.sendPacket(new ExSpawnEmitter(player, isShine ? SpawnEmitterType.WHITE_SOUL : SpawnEmitterType.BLACK_SOUL));
        player.sendPacket(new EtcStatusUpdate(player));
    }

    @Override
    public boolean isInstant() {
        return true;
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new ObtainSoul(data);
        }

        @Override
        public String effectName() {
            return "obtain-soul";
        }
    }
}
