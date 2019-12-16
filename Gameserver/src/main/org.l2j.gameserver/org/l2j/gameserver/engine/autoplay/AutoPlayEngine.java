package org.l2j.gameserver.engine.autoplay;

import io.github.joealisson.primitive.HashIntSet;
import io.github.joealisson.primitive.IntSet;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.handler.ItemHandler;
import org.l2j.gameserver.model.Shortcut;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.serverpackets.autoplay.ExAutoPlayDoMacro;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.ZoneType;

import java.util.Comparator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isMonster;

/**
 * @author JoeAlisson
 */
public final class AutoPlayEngine {

    private static final int AUTO_PLAY_INTERVAL = 2000;
    private final Set<Player> players = ConcurrentHashMap.newKeySet();
    private final Set<Player> autoPotionPlayers = ConcurrentHashMap.newKeySet();
    private final Map<Player, IntSet> autoSuppliesPlayers = new ConcurrentHashMap<>();
    private final DoMacro doMacroTask = new DoMacro();
    private final DoAutoSupply doAutoSupply = new DoAutoSupply();
    private ScheduledFuture<?> autoPlayTask;
    private ScheduledFuture<?> autoSupplyTask;
    private final Object autoPlayTaskLocker = new Object();
    private final Object autoSupplyTaskLocker = new Object();

    private AutoPlayEngine() {

    }

    public void stopTasks(Player player) {
        stopAutoPlay(player);
        stopAutoPotion(player);

    }

    public void startAutoPlay(Player player) {
        players.add(player);
        synchronized (autoPlayTaskLocker) {
            if(isNull(autoPlayTask)) {
                autoPlayTask = ThreadPool.scheduleAtFixedDelay(doMacroTask, AUTO_PLAY_INTERVAL, AUTO_PLAY_INTERVAL);
            }
        }
    }

    public void stopAutoPlay(Player player) {
        players.remove(player);
        synchronized (autoPlayTaskLocker) {
            if (players.isEmpty() && nonNull(autoPlayTask)) {
                autoPlayTask.cancel(false);
                autoPlayTask = null;
            }
        }
    }

    public void startAutoPotion(Player player) {
        autoPotionPlayers.add(player);
        synchronized (autoSupplyTaskLocker) {
            if(isNull(autoSupplyTask)) {
                autoSupplyTask = ThreadPool.scheduleAtFixedDelay(doAutoSupply, AUTO_PLAY_INTERVAL, AUTO_PLAY_INTERVAL);
            }
        }
    }

    public void stopAutoPotion(Player player) {
        autoPotionPlayers.remove(player);
        synchronized (autoSupplyTaskLocker) {
            if(autoPotionPlayers.isEmpty() && autoSuppliesPlayers.isEmpty() && nonNull(autoSupplyTask)) {
                autoSupplyTask.cancel(false);
                autoSupplyTask = null;
            }
        }
    }

    public void startAutoSupply(Player player, int shortcutClientId) {
        autoSuppliesPlayers.computeIfAbsent(player, p -> new HashIntSet()).add(shortcutClientId);
        synchronized (autoSupplyTaskLocker) {
            if(isNull(autoSupplyTask)) {
                autoSupplyTask = ThreadPool.scheduleAtFixedDelay(doAutoSupply, AUTO_PLAY_INTERVAL, AUTO_PLAY_INTERVAL);
            }
        }
    }

    public void stopAutoSupply(Player player, int shortcutClientId) {
        var shortcuts = autoSuppliesPlayers.get(player);
        if(nonNull(shortcuts)) {
            shortcuts.remove(shortcutClientId);
            if(shortcuts.isEmpty()) {
                stopAutoSupply(player);
            }
        }
    }

    public void stopAutoSupply(Player player) {
        autoSuppliesPlayers.remove(player);
        synchronized (autoSupplyTaskLocker) {
            if(autoSuppliesPlayers.isEmpty() && autoPotionPlayers.isEmpty() && nonNull(autoSupplyTask)) {
                autoSupplyTask.cancel(false);
                autoSupplyTask = null;
            }
        }
    }

    public static AutoPlayEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static final class Singleton {
        private static final AutoPlayEngine INSTANCE = new AutoPlayEngine();
    }

    private final class DoMacro implements Runnable {

        @Override
        public void run() {
            var world = World.getInstance();

            players.parallelStream().forEach(player -> {
                if(isNull(player) || player.getAI().getIntention() == CtrlIntention.AI_INTENTION_PICK_UP || player.isInsideZone(ZoneType.PEACE))  {
                    return;
                }

                var setting = player.getAutoPlaySettings();
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
        private boolean canBeTargeted(Player player, AutoPlaySettings setting, Monster monster) {
            return !monster.isDead() && monster.isAutoAttackable(player) && (!setting.isRespectfulMode() || isNull(monster.getTarget()) || monster.getTarget().equals(player)) && GeoEngine.getInstance().canSeeTarget(player, monster);
        }
    }

    private final class DoAutoSupply implements Runnable {

        @Override
        public void run() {
            var it = autoPotionPlayers.iterator();
            while (it.hasNext()) {
                var player = it.next();
                var settings = player.getAutoPlaySettings();
                if(settings.getUsableHpPotionPercent() >= player.getCurrentHpPercent()) {

                    var shortcut = player.getShortCut(Shortcut.AUTO_POTION_SLOT, Shortcut.AUTO_PLAY_PAGE) ;
                    if(nonNull(shortcut))  {
                        var item = player.getInventory().getItemByObjectId(shortcut.getId());
                        useItem(player, item);
                    } else {
                        it.remove();
                    }
                }
            }
        }

        private void useItem(Player player, Item item) {
            var reuseDelay = item.getReuseDelay();
            if (reuseDelay <= 0 || player.getItemRemainingReuseTime(item.getObjectId()) <= 0) {
                var etcItem = item.getEtcItem();
                var handler = ItemHandler.getInstance().getHandler(etcItem);

                if (nonNull(handler) && handler.useItem(player, item, false) && reuseDelay > 0) {
                    player.addTimeStampItem(item, reuseDelay);
                }
            }
        }
    }
}
