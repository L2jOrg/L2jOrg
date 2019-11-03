package org.l2j.gameserver.engine.autoplay;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.serverpackets.autoplay.ExAutoPlayDoMacro;
import org.l2j.gameserver.world.World;

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
            var it = players.entrySet().iterator();
            while (it.hasNext()) {
                var entry = it.next();
                var player = entry.getKey();
                if(isNull(player))  {
                    it.remove();
                } else {
                    var setting = entry.getValue();
                    var range = setting.isNearTarget() ? 600 : 1400;

                    /**
                     * 91974, 91912, 1540, 49528, 49533, 91251
                     * 91690, 91843, 91857
                     */

                    if(setting.isAutoPickUpOn()) {
                        world.forAnyVisibleObjectInRange(player, Item.class, range,
                                item -> player.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, item),
                                item -> item.getDropProtection().tryPickUp(player));
                    }

                    if(player.getAI().getIntention() == CtrlIntention.AI_INTENTION_PICK_UP) {
                        continue;
                    }

                    var target = player.getTarget();
                    if(isNull(target) || (isMonster(target) && ((Monster) target).isDead())) {
                        var monster = world.findAnyVisibleObject(player, Monster.class, range, false, m -> canBeTargeted(player, setting, m));
                        player.setTarget(monster);
                    }
                    if(nonNull(player.getTarget())) {
                        player.sendPacket(ExAutoPlayDoMacro.STATIC);
                    }
                }
            }
        }

        private boolean canBeTargeted(Player player, AutoPlaySetting setting, Monster monster) {
            var attackerList = monster.getAggroList();
            return !monster.isDead() && monster.isAutoAttackable(player) && (!setting.isMannerMode() || attackerList.isEmpty() || attackerList.size() == 1 && attackerList.containsKey(player));
        }
    }

    private static final class Singleton {
        private static final AutoPlayEngine INSTANCE = new AutoPlayEngine();
    }
}
