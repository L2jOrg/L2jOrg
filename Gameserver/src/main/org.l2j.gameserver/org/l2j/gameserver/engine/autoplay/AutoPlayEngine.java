package org.l2j.gameserver.engine.autoplay;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.data.database.data.Shortcut;
import org.l2j.gameserver.data.xml.ActionManager;
import org.l2j.gameserver.engine.geo.GeoEngine;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.handler.ItemHandler;
import org.l2j.gameserver.handler.PlayerActionHandler;
import org.l2j.gameserver.model.actor.instance.Monster;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.serverpackets.ExBasicActionList;
import org.l2j.gameserver.util.MathUtil;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.world.zone.ZoneType;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.util.GameUtils.isMonster;

/**
 * @author JoeAlisson
 */
public final class AutoPlayEngine {

    private static final int AUTO_PLAY_INTERVAL = 2000;

    private final ForkJoinPool autoPlayPool = new ForkJoinPool();
    private final Set<Player> players = ConcurrentHashMap.newKeySet();
    private final Set<Player> autoPotionPlayers = ConcurrentHashMap.newKeySet();

    private final DoAutoPlay doAutoPlayTask = new DoAutoPlay();
    private final Object autoPlayTaskLocker = new Object();
    private ScheduledFuture<?> autoPlayTask;

    private final DoAutoPotion doAutoPotion = new DoAutoPotion();
    private final Object autoPotionTaskLocker = new Object();
    private ScheduledFuture<?> autoPotionTask;

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
    }

    public void stopAutoPlay(Player player) {
        players.remove(player);
        synchronized (autoPlayTaskLocker) {
            if (players.isEmpty() && nonNull(autoPlayTask)) {
                autoPlayTask.cancel(false);
                autoPlayTask = null;
            }
        }
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
        autoPotionPlayers.remove(player);
        synchronized (autoPotionTaskLocker) {
            if(autoPotionPlayers.isEmpty()  && nonNull(autoPotionTask)) {
                autoPotionTask.cancel(false);
                autoPotionTask = null;
            }
        }
    }

    public void stopTasks(Player player) {
        stopAutoPlay(player);
        stopAutoPotion(player);
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
                    var item = World.getInstance().findAnyVisibleObject(player, Item.class, range, false, it -> it.getDropProtection().tryPickUp(player));
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
            var target = player.getTarget();
            if ((isNull(target) || (isMonster(target) && ((Monster) target).isDead()) || target.equals(player)) && !player.isTargetingDisabled()) {
                var monster = World.getInstance().findFirstVisibleObject(player, Monster.class, range, false, m -> canBeTargeted(player, setting, m), Comparator.comparingDouble(m -> MathUtil.calculateDistanceSq3D(player, m)));
                player.setTarget(monster);
            }

            if (nonNull(player.getTarget())) {
                tryUseAutoShortcut(player);
            }
        }

        private void tryUseAutoShortcut(Player player) {
            var nextShortcut = player.nextAutoShortcut();
            if(nonNull(nextShortcut)) {
                var  shortcut = nextShortcut;
                do {
                    if(useShortcut(player, shortcut)) {
                        break;
                    }
                    shortcut = player.nextAutoShortcut();
                } while (!nextShortcut.equals(shortcut));
            }
        }

        private boolean useShortcut(Player player, Shortcut shortcut) {
            return switch (shortcut.getType()) {
                case SKILL -> autoUseSkill(player, shortcut);
                case ITEM -> autoUseItem(player, shortcut);
                case ACTION -> autoUseAction(player, shortcut);
                default -> false;
            };
        }

        private boolean autoUseAction(Player player, Shortcut shortcut) {
            var actionId = shortcut.getShortcutId();
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
                }
                return true;
            }
            return false;
        }

        private boolean autoUseItem(Player player, Shortcut shortcut) {
            var item = player.getInventory().getItemByObjectId(shortcut.getShortcutId());
            if(nonNull(item) && item.isAutoSupply() && item.getTemplate().checkAnySkill(ItemSkillType.NORMAL, s -> !(player.isAffectedBySkill(s) || player.hasAbnormalType(s.getSkill().getAbnormalType())))) {
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
            if(skill.isAutoBuff() && (player.hasAbnormalType(skill.getAbnormalType()) || player.isAffectedBySkill(skill.getId()))) {
                return false;
            }

            player.onActionRequest();
            return player.useMagic(skill, null, false, false);
        }

        private boolean canBeTargeted(Player player, AutoPlaySettings setting, Monster monster) {
            return  monster.isTargetable() && !monster.isDead() && monster.isAutoAttackable(player) && (!setting.isRespectfulMode() || hasBeenAggresive(player, monster)) &&
                    GeoEngine.getInstance().canSeeTarget(player, monster) && GeoEngine.getInstance().canMoveToTarget(player, monster);
        }

        private boolean hasBeenAggresive(Player player, Monster monster) {
            return isNull(monster.getTarget()) || monster.getTarget().equals(player) || monster.getAggroList().isEmpty() || isInAggroList(monster, player);
        }

        private boolean isInAggroList(Monster monster, Player player) {
            var aggroList = monster.getAggroList();
            if(aggroList.containsKey(player)) {
                return true;
            }
            if(player.hasPet() && (monster.getTarget().equals(player.getPet()) || aggroList.containsKey(player.getPet()))) {
                return true;
            }
            return player.hasServitors() &&  player.getServitors().values().stream().anyMatch(s -> monster.getTarget().equals(s) || aggroList.containsKey(s));
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

            if (nonNull(handler) && handler.useItem(player, item, false) && reuseDelay > 0) {
                player.onActionRequest();
                player.addTimeStampItem(item, reuseDelay);
            }
        }
    }

    private boolean canUseAutoPlay(Player player) {
        return !player.getAutoPlaySettings().isAutoPlaying() &&
                player.getAI().getIntention() != CtrlIntention.AI_INTENTION_PICK_UP &&
                !player.hasBlockActions() &&
                !player.isControlBlocked() &&
                !player.isAlikeDead() &&
                !player.isInsideZone(ZoneType.PEACE) &&
                !player.inObserverMode() &&
                !player.isCastingNow();
    }
}
