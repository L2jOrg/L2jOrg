/*
 * Copyright © 2019-2021 L2JOrg
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
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.ShortcutType;
import org.l2j.gameserver.handler.ItemHandler;
import org.l2j.gameserver.handler.PlayerActionHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExBasicActionList;
import org.l2j.gameserver.network.serverpackets.autoplay.ExAutoPlaySettingResponse;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.ZoneType;

import java.util.Arrays;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.function.Supplier;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.commons.util.Util.isNullOrEmpty;

/**
 * @author JoeAlisson
 * && fine-tunning by Bru7aLMike
 */
public final class AutoPlayEngine {

    private static final int AUTO_PLAY_INTERVAL = 500;
    private static final int AUTO_SUPPLY_INTERVAL = 1000;
    private static final int DEFAULT_ACTION = 2;

    private final Set<Player> players = ConcurrentHashMap.newKeySet();
    private final Set<Player> autoSupplyPlayers = ConcurrentHashMap.newKeySet();

    private final DoAutoPlay doAutoPlayTask = new DoAutoPlay();
    private final Object autoPlayTaskLocker = new Object();
    private ScheduledFuture<?> autoPlayTask;

    private final DoAutoSupply doAutoSupply = new DoAutoSupply();
    private final Object autoSupplyTaskLocker = new Object();
    private ScheduledFuture<?> autoSupplyTask;

    private final AutoPlayTargetFinder tauntFinder = new TauntFinder();
    private final AutoPlayTargetFinder monsterFinder = new MonsterFinder();
    private final AutoPlayTargetFinder playerFinder = new PlayerFinder();
    private final AutoPlayTargetFinder friendlyFinder = new FriendlyMobFinder();

    private AutoPlayEngine() {
    }

    public boolean setActiveAutoShortcut(Player player, int room, boolean activate) {
        var shortcut = player.getShortcut(room);

        if(isNull(shortcut)) {
            return false;
        }

        if(isAutoSupply(player, shortcut)) {
            player.setActiveAutoSupplyShortcut(room, activate);
            startAutoSupply(player);
            return true;
        } else if(handleShortcut(player, shortcut)) {
            player.setActiveAutoShortcut(room, activate);
            return true;
        }
        return false;
    }

    public boolean isAutoSupply(Player player, Shortcut shortcut) {
        return (shortcut.getType() == ShortcutType.ITEM && handleAutoItem(player, shortcut)) ||
               (shortcut.getType() == ShortcutType.SKILL && handleAutoSupplySkill(player, shortcut));
    }

    private boolean handleAutoSupplySkill(Player player, Shortcut shortcut) {
        var skill = player.getKnownSkill(shortcut.getShortcutId());
        if(isNull(skill)) {
            player.deleteShortcut(shortcut.getClientId());
            return false;
        }

        return skill.isAutoBuff() || skill.isAutoTransformation();
    }

    private boolean handleShortcut(Player player, Shortcut shortcut) {
        return (shortcut.getType() == ShortcutType.SKILL && handleAutoSkill(player, shortcut)) ||
               (shortcut.getType() == ShortcutType.ACTION && handleAutoAction(shortcut));
    }

    private boolean handleAutoAction(Shortcut shortcut) {
        return ActionManager.getInstance().isAutoUseAction(shortcut.getShortcutId());
    }

    private boolean handleAutoSkill(Player player, Shortcut shortcut) {
        var skill = player.getKnownSkill(shortcut.getShortcutId());
        if(isNull(skill)) {
            player.deleteShortcut(shortcut.getClientId());
            return false;
        }
        return skill.isAutoUse();
    }

    private boolean handleAutoItem(Player player, Shortcut shortcut) {
        var item = player.getInventory().getItemByObjectId(shortcut.getShortcutId());
        if (isNull(item)) {
            player.deleteShortcut(shortcut.getClientId());
            return false;
        }
        return item.isAutoSupply() || item.isAutoPotion();
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

    private void startAutoSupply(Player player) {
        if(autoSupplyPlayers.add(player)) {
            synchronized (autoSupplyTaskLocker) {
                if (isNull(autoSupplyTask)) {
                    autoSupplyTask = ThreadPool.scheduleAtFixedDelay(doAutoSupply, AUTO_SUPPLY_INTERVAL, AUTO_SUPPLY_INTERVAL);
                }
            }
        }
    }

    private void stopAutoSupply(Player player) {
        if(autoSupplyPlayers.remove(player)) {
            stopAutoSupplyTask();
        }
    }

    private void stopAutoSupplyTask() {
        synchronized (autoSupplyTaskLocker) {
            if (autoSupplyPlayers.isEmpty() && nonNull(autoSupplyTask)) {
                autoSupplyTask.cancel(false);
                autoSupplyTask = null;
            }
        }
    }

    public void stopTasks(Player player) {
        if(nonNull(player.getAutoPlaySettings())) {
            stopAutoPlay(player);
            stopAutoSupply(player);
        }
    }

    public static AutoPlayEngine getInstance() {
        return Singleton.INSTANCE;
    }

    private static final class Singleton {
        private static final AutoPlayEngine INSTANCE = new AutoPlayEngine();
    }

    private final class DoAutoPlay implements Runnable {

        @Override
        public void run() {
            for (Player player : players) {
                if(!player.getAutoPlaySettings().isAutoPlaying() && canDoAutoAction(player)) {
                    ThreadPool.executeForked(() -> doNextAction(player));
                }
            }
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

            if(!targetFinder.canBeTarget(player, player.getTarget(), range)) {
                player.setTarget(targetFinder.findNextTarget(player, range));
            }

            if (nonNull(player.getTarget())) {
                tryUseAutoShortcut(player);
                if(player.hasSummon()) {
                    tryUseAutoSummonShortcut(player);
                }
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
            var used = tryUseAutoShortcut(player, player::nextAutoShortcut);
            if(!(used || player.isMageClass())) {
                autoUseAction(player, DEFAULT_ACTION);
            }
        }

        private void tryUseAutoSummonShortcut(Player player) {
            tryUseAutoShortcut(player, player::nextAutoSummonShortcut);
        }

        private boolean tryUseAutoShortcut(Player player, Supplier<Shortcut> supplier) {
            var nextShortcut = supplier.get();
            if(nonNull(nextShortcut)) {
                var  shortcut = nextShortcut;
                do {
                    if(useShortcut(player, shortcut)) {
                        return true;
                    }
                    shortcut = supplier.get();
                } while (!nextShortcut.equals(shortcut));
            }
            return false;
        }

        private boolean useShortcut(Player player, Shortcut shortcut) {
            return (shortcut.getType() == ShortcutType.SKILL && autoUseSkill(player, shortcut)) ||
                   (shortcut.getType() == ShortcutType.ACTION) && autoUseAction(player, shortcut.getShortcutId());
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

        private boolean autoUseSkill(Player player, Shortcut shortcut) {
            var skill = player.getKnownSkill(shortcut.getShortcutId());
            if(isNull(skill)) {
                player.deleteShortcut(shortcut.getShortcutId());
                return false;
            }
            return useSkill(player, skill);
        }
    }

    private final class DoAutoSupply implements Runnable {

        @Override
        public void run() {
            if(autoSupplyPlayers.isEmpty()) {
                stopAutoSupplyTask();
                return;
            }

            var it = autoSupplyPlayers.iterator();
            while (it.hasNext()) {
                var player = it.next();

                if(!canDoAutoAction(player)) {
                    continue;
                }

                var shortcuts =  player.getActiveAutoSupplies();
                if(isNullOrEmpty(shortcuts)) {
                    it.remove();
                } else {
                    ThreadPool.executeForked(() -> autoSupply(player));
                }
            }
        }

        private void autoSupply(Player player) {
            if(!canDoAutoAction(player)) {
                return;
            }

            var it = player.getActiveAutoSupplies().iterator();
            while (it.hasNext()) {
                var shortcut = it.next();
                if(!useSupplyShortcut(player, shortcut)) {
                    it.remove();
                    player.deleteShortcut(shortcut.getClientId());
                }
            }
        }

        private boolean useSupplyShortcut(Player player, Shortcut shortcut) {
            return (shortcut.getType() == ShortcutType.SKILL && useSkillShortcut(player, shortcut)) ||
                   (shortcut.getType() == ShortcutType.ITEM && useItemShortcut(player, shortcut));
        }

        private boolean useItemShortcut(Player player, Shortcut shortcut) {
            var item = player.getInventory().getItemByObjectId(shortcut.getShortcutId());
            if (nonNull(item)) {
                useItem(player, item);
                return true;
            }
            return false;
        }

        private void useItem(Player player, Item item) {
            if (checkReuseRestriction(player, item)) {
                var etcItem = item.getEtcItem();
                var handler = ItemHandler.getInstance().getHandler(etcItem);

                if (nonNull(handler) && handler.useItem(player, item, false)) {
                    player.onActionRequest();
                    if(item.getReuseDelay() > 0) {
                        player.addTimeStampItem(item, item.getReuseDelay());
                    }
                }
            }
        }

        private boolean checkReuseRestriction(Player player, Item item) {
            var reuseDelay = item.getReuseDelay();
            return (reuseDelay <= 0 || player.getItemRemainingReuseTime(item.getObjectId()) <= 0) &&
                    ( (item.isAutoPotion() && player.getAutoPlaySettings().getUsableHpPotionPercent() > player.getCurrentHpPercent()) ||
                            (item.isAutoSupply() && isSupplyEffectReusable(player, item)));
        }

        private boolean isSupplyEffectReusable(Player player, Item item) {
            return player.getBuffRemainTimeByItemSkill(item) <= 3;
        }

        private boolean useSkillShortcut(Player player, Shortcut shortcut) {
            var skill = player.getKnownSkill(shortcut.getShortcutId());
            if(nonNull(skill)) {
                useSkill(player, skill);
                return true;
            }
            return false;
        }
    }

    private boolean useSkill(Player player, Skill skill) {
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
        return player.useSkill(skill, null, false, false);

    }

    private boolean canDoAutoAction(Player player) {
        return  player.getAI().getIntention() != CtrlIntention.AI_INTENTION_PICK_UP &&
                player.getAI().getIntention() != CtrlIntention.AI_INTENTION_CAST &&
                !player.hasBlockActions() &&
                !player.isControlBlocked() &&
                !player.isAlikeDead() &&
                !player.isInsideZone(ZoneType.PEACE) &&
                !player.isInObserverMode() &&
                !player.isAttackingNow() &&
                !player.isCastingNow();
    }
}
