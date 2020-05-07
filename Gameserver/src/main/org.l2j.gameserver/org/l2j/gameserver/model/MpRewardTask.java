package org.l2j.gameserver.model;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;

import java.util.concurrent.ScheduledFuture;

import static org.l2j.gameserver.util.GameUtils.isPlayer;

/**
 * @author UnAfraid
 */
public class MpRewardTask {
    private int _count;
    private final double _value;
    private final ScheduledFuture<?> _task;
    private final Creature _creature;

    public MpRewardTask(Creature creature, Npc npc) {
        final NpcTemplate template = npc.getTemplate();
        _creature = creature;
        _count = template.getMpRewardTicks();
        _value = calculateBaseValue(npc, creature);
        _task = ThreadPool.scheduleAtFixedRate(this::run, Config.EFFECT_TICK_RATIO, Config.EFFECT_TICK_RATIO);
    }

    /**
     * @param npc
     * @param creature
     * @return
     */
    private double calculateBaseValue(Npc npc, Creature creature) {
        final NpcTemplate template = npc.getTemplate();
        switch (template.getMpRewardType()) {
            case PER: {
                return (creature.getMaxMp() * (template.getMpRewardValue() / 100d)) / template.getMpRewardTicks();
            }
        }
        return template.getMpRewardValue() / (double) template.getMpRewardTicks();
    }

    private void run() {
        if ((--_count <= 0) || (isPlayer(_creature) && !_creature.getActingPlayer().isOnline())) {
            _task.cancel(false);
            return;
        }

        _creature.setCurrentMp(_creature.getCurrentMp() + _value);
    }
}
