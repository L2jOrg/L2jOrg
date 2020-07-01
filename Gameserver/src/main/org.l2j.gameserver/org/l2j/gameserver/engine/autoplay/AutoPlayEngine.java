/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.engine.autoplay;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.data.database.data.Shortcut;
import org.l2j.gameserver.data.xml.ActionManager;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.handler.ItemHandler;
import org.l2j.gameserver.handler.PlayerActionHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.item.instance.Item;
import org.l2j.gameserver.network.serverpackets.ExBasicActionList;
import org.l2j.gameserver.network.serverpackets.autoplay.ExAutoPlaySettingResponse;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.ZoneType;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public final class AutoPlayEngine {

    private static final int AUTO_PLAY_INTERVAL = 1000;
    private static final int DEFAULT_ACTION = 2;

    private final ForkJoinPool autoPlayPool = new ForkJoinPool();
    private final Set<Player> players = ConcurrentHashMap.newKeySet();
    private final Set<Player> autoPotionPlayers = ConcurrentHashMap.newKeySet();

    private final DoAutoPlay doAutoPlayTask = new DoAutoPlay();
    private final Object autoPlayTaskLocker = new Object();
    private ScheduledFuture<?> autoPlayTask;

    private final DoAutoPotion doAutoPotion = new DoAutoPotion();
    private final Object autoPotionTaskLocker = new Object();
    private ScheduledFuture<?> autoPotionTask;
    
    private final AutoPlayTargetFinder tauntFinder = new TauntFinder();
    private final AutoPlayTargetFinder monsterFinder = new MonsterFinder();
    private final AutoPlayTargetFinder playerFinder = new PlayerFinder();
    private final AutoPlayTargetFinder friendlyFinder = new FriendlyMobFinder();

    private AutoPlayEngine() {
    }

    public boolean setActiveAutoShortcut(Player player, int room, boolean activate) {
        var shortcut = player.getShortcut(room);

        if(nonNull(shortcut) && handleShortcut(player, shortcut, activate)) {
            player.setActiveAutoShortcut(room, activate);
            return true;
        }

        return false;
    }

    private boolean handleShortcut(Player player, Shortcut shortcut, boolean activate) {
        return switch (shortcut.getType()) {
            case ITEM -> handleAutoItem(player, shortcut, activate);
            case SKILL -> handleAutoSkill(player, shortcut);
            case ACTION -> handleAutoAction(shortcut);
            default -> false;
        };
    }

    private boolean handleAutoAction(Shortcut shortcut) {
        return ActionManager.getInstance().isAutoUseAction(shortcut.getShortcutId());
    }

    private boolean handleAutoSkill(Player player, Shortcut shortcut) {
        var skill = player.getKnownSkill(shortcut.getShortcutId());
        if(isNull(skill) || !skill.isAutoUse()) {
            player.deleteShortcut(shortcut.getClientId());
            return false;
        }
        return true;
    }

    private boolean handleAutoItem(Player player, Shortcut shortcut, boolean activate) {
        var item = player.getInventory().getItemByObjectId(shortcut.getShortcutId());
        if (isNull(item) || !(item.isAutoSupply() || item.isAutoPotion())) {
            player.deleteShortcut(shortcut.getClientId());
            return false;
        }
        if(item.isAutoPotion()) {
            if(activate) {
                startAutoPotion(player);
            } else {
                stopAutoPotion(player);
            }
        }
        return true;
    }

    public void startAutoPlay(Player player) {
        players.add(player);
        synchronized (autoPlayTaskLocker) {
            if(isNull(autoPlayTask)) {
                autoPlayTask = ThreadPool.scheduleAtFixedDelay(doAutoPlayTask, AUTO_PLAY_INTERVAL, AUTO_PLAY_INTERVAL);
            }
        }
        player.getAutoPlaySettings().setActive(true);
        player.sendPacket(new ExAutoPlaySettingResponse());
    }

    public void stopAutoPlay(Player player) {
        if(players.remove(player)) {
            synchronized (autoPlayTaskLocker) {
                if (players.isEmpty() && nonNull(autoPlayTask)) {
                    autoPlayTask.cancel(false);
                    autoPlayTask = null;
                }
            }
        }
        player.getAutoPlaySettings().setActive(false);
        player.sendPacket(new ExAutoPlaySettingResponse());
        player.resetNextAutoShortcut();
    }

    public void startAutoPotion(Player player) {
        autoPotionPlayers.add(player);
        synchronized (autoPotionTaskLocker) {
            if(isNull(autoPotionTask)) {
                autoPotionTask = ThreadPool.scheduleAtFixedDelay(doAutoPotion, AUTO_PLAY_INTERVAL, AUTO_PLAY_INTERVAL);
            }
        }
    }

    public void stopAutoPotion(Player player) {
        if(autoPotionPlayers.remove(player)) {
            synchronized (autoPotionTaskLocker) {
                if (autoPotionPlayers.isEmpty() && nonNull(autoPotionTask)) {
                    autoPotionTask.cancel(false);
                    autoPotionTask = null;
                }
            }
        }
    }

    public void stopTasks(Player player) {
        if(nonNull(player.getAutoPlaySettings())) {
            stopAutoPlay(player);
            stopAutoPotion(player);
        }
    }

    public static AutoPlayEngine getInstance() {
        return Singleton.INSTANCE;
    }

    public void shutdown() {
        autoPlayPool.shutdown();
    }

    private static final class Singleton {
        private static final AutoPlayEngine INSTANCE = new AutoPlayEngine();
    }

    private final class DoAutoPlay implements Runnable {

        @Override
        public void run() {
            autoPlayPool.submit(this::doAutoPlay);
        }

        private void doAutoPlay() {
            players.parallelStream().filter(AutoPlayEngine.this::canUseAutoPlay).forEach(this::doNextAction);
        }

        private void doNextAction(Player player) {
            var setting = player.getAutoPlaySettings();
            setting.setAutoPlaying(true);
            try {
                var range = setting.isNearTarget() ? 600 : 1400;

                if (setting.isAutoPickUpOn()) {
                    var item = World.getInstance().findAnyVisibleObject(player, Item.class, 200, false, it -> nonNull(it.getDropProtection().getOwner()) && it.getDropProtection().tryPickUp(player) && player.getInventory().validateCapacity(it));
                    if (nonNull(item)) {
                        player.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, item);
                        return;
                    }
                }

                pickTargetAndAct(player, setting, range);
            } finally {
                setting.setAutoPlaying(false);
            }
        }

        private void pickTargetAndAct(Player player, AutoPlaySettings setting, int range) {
            var targetFinder = targetFinderBySettings(setting);

            if(!targetFinder.canBeTarget(player, player.getTarget())) {
                player.setTarget(targetFinder.findNextTarget(player, range));
            }

            if (nonNull(player.getTarget())) {
                tryUseAutoShortcut(player);
            }
        }

        private AutoPlayTargetFinder targetFinderBySettings(AutoPlaySettings setting) {
            return switch (setting.getNextTargetMode()) {
                case 0 -> tauntFinder;
                case 2 -> playerFinder;
                case 3 -> friendlyFinder;
                default -> monsterFinder;
            };
        }

        private void tryUseAutoShortcut(Player player) {
            var nextShortcut = player.nextAutoShortcut();
            if(nonNull(nextShortcut)) {
                var  shortcut = nextShortcut;
                do {
                    if(useShortcut(player, shortcut)) {
                        return;
                    }
                    shortcut = player.nextAutoShortcut();
                } while (!nextShortcut.equals(shortcut));
            }
            autoUseAction(player, DEFAULT_ACTION);
        }

        private boolean useShortcut(Player player, Shortcut shortcut) {
            return switch (shortcut.getType()) {
                case SKILL -> autoUseSkill(player, shortcut);
                case ITEM -> autoUseItem(player, shortcut);
                case ACTION -> autoUseAction(player, shortcut.getShortcutId());
                default -> false;
            };
        }

        private boolean autoUseAction(Player player, int actionId) {
            final int[] allowedActions = player.isTransformed() ? ExBasicActionList.ACTIONS_ON_TRANSFORM : ExBasicActionList.DEFAULT_ACTION_LIST;
            if (Arrays.binarySearch(allowedActions, actionId)  < 0) {
                return false;
            }

            var action = ActionManager.getInstance().getActionData(actionId);
            if (nonNull(action)) {
                var handler = PlayerActionHandler.getInstance().getHandler(action.getHandler());
                if (nonNull(handler)) {
                    handler.useAction(player, action, false, false);
                    player.onActionRequest();
                    return true;
                }
            }
            return false;
        }

        private boolean autoUseItem(Player player, Shortcut shortcut) {
            var item = player.getInventory().getItemByObjectId(shortcut.getShortcutId());
            if(nonNull(item) && item.isAutoSupply() && item.getTemplate().checkAnySkill(ItemSkillType.NORMAL, s -> player.getBuffRemainTimeBySkillOrAbormalType(s.getSkill()) <= 3)) {
                useItem(player, item);
                return true;
            }
            return false;
        }

        private boolean autoUseSkill(Player player, Shortcut shortcut) {
            var skill = player.getKnownSkill(shortcut.getShortcutId());

            if (skill.isBlockActionUseSkill()) {
                return false;
            }

            if(skill.isAutoTransformation() && player.isTransformed()) {
                return false;
            }
            if(skill.isAutoBuff() && player.getBuffRemainTimeBySkillOrAbormalType(skill) > 3) {
                return false;
            }

            player.onActionRequest();
            return player.useMagic(skill, null, false, false);
        }
    }

    private final class DoAutoPotion implements Runnable {

        @Override
        public void run() {
            autoPlayPool.submit(this::useAutoPotion);
        }

        private void useAutoPotion() {
            var toRemove = new HashSet<Player>();
            autoPotionPlayers.parallelStream().filter(this::canUseAutoPotion).forEach(player -> {
                var shortcut = player.getShortcut(Shortcut.AUTO_POTION_ROOM) ;
                if(nonNull(shortcut))  {
                    var item = player.getInventory().getItemByObjectId(shortcut.getShortcutId());
                    if(nonNull(item)) {
                        useItem(player, item);
                    }
                } else {
                   toRemove.add(player);
                }
            });
            if(!toRemove.isEmpty()) {
                autoPotionPlayers.removeAll(toRemove);
            }
        }

        private boolean canUseAutoPotion(Player player) {
            return canUseAutoPlay(player) && player.getAutoPlaySettings().getUsableHpPotionPercent() >= player.getCurrentHpPercent();
        }
    }

    private void useItem(Player player, Item item) {
        var reuseDelay = item.getReuseDelay();
        if (reuseDelay <= 0 || player.getItemRemainingReuseTime(item.getObjectId()) <= 0) {
            var etcItem = item.getEtcItem();
            var handler = ItemHandler.getInstance().getHandler(etcItem);

            if (nonNull(handler) && handler.useItem(player, item, false)) {
                player.onActionRequest();
                if(reuseDelay > 0) {
                    player.addTimeStampItem(item, reuseDelay);
                }
            }
        }
    }

    private boolean canUseAutoPlay(Player player) {
        return !player.getAutoPlaySettings().isAutoPlaying() &&
                player.getAI().getIntention() != CtrlIntention.AI_INTENTION_PICK_UP &&
                player.getAI().getIntention() != CtrlIntention.AI_INTENTION_CAST &&
                !player.hasBlockActions() &&
                !player.isControlBlocked() &&
                !player.isAlikeDead() &&
                !player.inObserverMode() &&
                !player.isCastingNow();
    }
}
