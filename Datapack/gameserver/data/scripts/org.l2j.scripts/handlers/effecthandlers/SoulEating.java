package handlers.effecthandlers;

import org.l2j.gameserver.engine.skill.api.SkillEffectFactory;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.Playable;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.effects.AbstractEffect;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayableExpChanged;
import org.l2j.gameserver.model.events.listeners.ConsumerEventListener;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.model.stats.Stat;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExSpawnEmitter;
import org.l2j.gameserver.network.serverpackets.ExSpawnEmitter.SpawnEmitterType;

import static org.l2j.gameserver.util.GameUtils.isNpc;
import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * Soul Eating effect implementation.
 * @author UnAfraid
 * @author JoeAlisson
 */
public final class SoulEating extends AbstractEffect {
    private final int expNeeded;
    private final int power;

    private SoulEating(StatsSet params) {
        expNeeded = params.getInt("experience");
        power = params.getInt("power");
    }

    @Override
    public void onStart(Creature effector, Creature effected, Skill skill, Item item) {
        if (isPlayer(effected)) {
            effected.addListener(new ConsumerEventListener(effected, EventType.ON_PLAYABLE_EXP_CHANGED, (OnPlayableExpChanged event) -> onExperienceReceived(event.getPlayable(), (event.getNewExp() - event.getOldExp())), this));
        }
    }

    @Override
    public void onExit(Creature effector, Creature effected, Skill skill) {
        if (isPlayer(effected)) {
            effected.removeListenerIf(EventType.ON_PLAYABLE_EXP_CHANGED, listener -> listener.getOwner() == this);
        }
    }

    @Override
    public void pump(Creature effected, Skill skill)
    {
        effected.getStats().mergeAdd(Stat.MAX_SOULS, power);
    }

    private void onExperienceReceived(Playable playable, long exp) {
        // TODO: Verify logic.
        if (isPlayer(playable) && (exp >= expNeeded)) {
            final Player player = playable.getActingPlayer();
            final int maxSouls = (int) player.getStats().getValue(Stat.MAX_SOULS, 0);

            if (player.getChargedSouls() >= maxSouls) {
                playable.sendPacket(SystemMessageId.SOUL_CANNOT_BE_ABSORBED_ANYMORE);
                return;
            }

            player.increaseSouls(1);

            if (isNpc(player.getTarget())) {
                final Npc npc = (Npc) playable.getTarget();
                player.broadcastPacket(new ExSpawnEmitter(player, npc, SpawnEmitterType.BLUE_SOUL_EATEN), 500);
            }
        }
    }

    public static class Factory implements SkillEffectFactory {

        @Override
        public AbstractEffect create(StatsSet data) {
            return new SoulEating(data);
        }

        @Override
        public String effectName() {
            return "soul-eating";
        }
    }
}
