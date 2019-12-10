package org.l2j.gameserver.engine.autoplay;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.serverpackets.autoplay.ExAutoPlayDoMacro;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.world.World;

import java.util.Comparator;
import java.util.WeakHashMap;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isMonster;

/**
 * @author JoeAlisson
 */
public final class AutoPlayEngine {

    private static final int AUTO_PLAY_INTERVAL = 2000;
    private final WeakHashMap<Player, AutoPlaySetting> players = new WeakHashMap<>();
    private final DoMacro doMacroTask = new DoMacro();
    private ScheduledFuture<?> scheduled;
    private final Object taskLocker = new Object();

    private AutoPlayEngine() {
    }

    public void startAutoPlay(Player player, AutoPlaySetting setting) {
        players.put(player, setting);
        synchronized (taskLocker) {
            if(isNull(scheduled)) {
                scheduled = ThreadPool.scheduleAtFixedDelay(doMacroTask, AUTO_PLAY_INTERVAL, AUTO_PLAY_INTERVAL);
            }
        }
    }

    public void stopAutoPlay(Player player) {
        players.remove(player);
        synchronized (taskLocker) {
            if (players.isEmpty() && nonNull(scheduled)) {
                scheduled.cancel(false);
                scheduled = null;
            }
        }
    }

    public static AutoPlayEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private final class DoMacro implements Runnable {

        @Override
        public void run() {
            var world = World.getInstance();

            players.entrySet().parallelStream().forEach(entry -> {
                var player = entry.getKey();
                if(isNull(player) || player.getAI().getIntention() == CtrlIntention.AI_INTENTION_PICK_UP)  {
                    return;
                }

                var setting = entry.getValue();

                var range = setting.isNearTarget() ? 600 : 1400;

                if(setting.isAutoPickUpOn()) {
                    var item = world.findAnyVisibleObject(player, Item.class, range, false, it -> it.getDropProtection().tryPickUp(player));
                    if(nonNull(item)) {
                        player.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, item);
                        return;
                    }
                }

                var target = player.getTarget();
                if(isNull(target) || (isMonster(target) && ((Monster) target).isDead())) {
                    var monster = world.findFirstVisibleObject(player, Monster.class, range, false, m -> canBeTargeted(player, setting, m), Comparator.comparingDouble(m -> MathUtil.calculateDistanceSq3D(player, m)));
                    player.setTarget(monster);
                }
                if(nonNull(player.getTarget())) {
                    player.sendPacket(ExAutoPlayDoMacro.STATIC);
                }

            });
        }

        private boolean canBeTargeted(Player player, AutoPlaySetting setting, Monster monster) {
            return !monster.isDead() && monster.isAutoAttackable(player) && (!setting.isRespectfulMode() || isNull(monster.getTarget()) || monster.getTarget().equals(player)) && GeoEngine.getInstance().canSeeTarget(player, monster);
        }
    }

    private static final class Singleton {
        private static final AutoPlayEngine INSTANCE = new AutoPlayEngine();
    }
}
