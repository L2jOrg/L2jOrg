package org.l2j.gameserver.taskmanager;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.engine.skill.api.Skill;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.handler.ItemHandler;
import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.holders.ItemSkillHolder;
import org.l2j.gameserver.model.item.EtcItem;
import org.l2j.gameserver.model.skills.targets.AffectScope;
import org.l2j.gameserver.world.zone.ZoneType;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import static org.l2j.gameserver.util.GameUtils.isCreature;
import static org.l2j.gameserver.util.GameUtils.isPlayable;

public class AutoUseTaskManager {
    private static final Set<Player> PLAYERS = ConcurrentHashMap.newKeySet();
    private static boolean _working = false;

    public AutoUseTaskManager()
    {
        ThreadPool.scheduleAtFixedRate(() ->
        {
            if (_working)
            {
                return;
            }
            _working = true;

            for (Player player : PLAYERS)
            {
                if (!player.isOnline())
                {
                    stopAutoUseTask(player);
                    continue;
                }

                final WorldObject target = player.getTarget();
                if (isCreature(target)) {
                    if (((Creature) target).isDead()) {
                        continue;
                    }
                }

                if (player.hasBlockActions() || player.isControlBlocked() || player.isAlikeDead() || player.isInsideZone(ZoneType.PEACE))
                {
                    continue;
                }
                if (Config.AUTO_USE_ITEM)
                {
                    ITEMS: for (Integer itemId : player.getAutoUseSettings().getAutoSupplyItems())
                    {
                        final Item item = player.getInventory().getItemByItemId(itemId);
                        if (item == null)
                        {
                            player.getAutoUseSettings().getAutoSupplyItems().remove(itemId);
                            continue; // TODO: break?
                        }

                        for (ItemSkillHolder itemSkillHolder : (item.getSkills(ItemSkillType.NORMAL)))
                        {
                            final Skill skill = itemSkillHolder.getSkill();
                            if (player.isAffectedBySkill(skill.getId()) || player.hasSkillReuse(skill.getReuseHashCode()) || !skill.checkCondition(player, player))
                            {
                                continue ITEMS;
                            }
                        }

                        final int reuseDelay = item.getReuseDelay();
                        if ((reuseDelay <= 0) || (player.getItemRemainingReuseTime(item.getObjectId()) <= 0))
                        {
                            final EtcItem etcItem = item.getEtcItem();
                            final IItemHandler handler = ItemHandler.getInstance().getHandler(etcItem);
                            if ((handler != null) && handler.useItem(player, item, false) && (reuseDelay > 0))
                            {
                                player.addTimeStampItem(item, reuseDelay);
                            }
                        }
                    }
                }
                if (Config.AUTO_USE_BUFF)
                {
                    for (Integer skillId : player.getAutoUseSettings().getAutoSkills()) {
                        final Skill skill = player.getKnownSkill(skillId);
                        if (skill == null) {
                            player.getAutoUseSettings().getAutoSkills().remove(skillId);
                            continue; // TODO: break?
                        }

                        if (!player.isAffectedBySkill(skillId) && !player.hasSkillReuse(skill.getReuseHashCode()) && skill.checkCondition(player, player)) {
                            // Summon check.
                            if (skill.getAffectScope() == AffectScope.SUMMON_EXCEPT_MASTER) {
                                if (!player.hasServitors()) // Is this check truly needed?
                                {
                                    continue;
                                }
                                int occurrences = 0;
                                for (Summon servitor : player.getServitors().values()) {
                                    if (servitor.isAffectedBySkill(skillId)) {
                                        occurrences++;
                                    }
                                }
                                if (occurrences == player.getServitors().size()) {
                                    continue;
                                }
                            }

                            if (!skill.isBad() && (!isPlayable(player))){
                            //if (skill.isAutoUse() && skill.isAutoBuff() && !(player.getCurrentMp() < (skill.getMpConsume() + skill.getMpInitialConsume()) || player.getAttackType().isRanged() && player.isAttackingDisabled())) {
                                player.doCast(skill);
                            }
                        }
                    }
                }
            }

            _working = false;
        }, 1000, 1000);
    }

    public void startAutoUseTask(Player player)
    {
        PLAYERS.add(player);
    }

    public void stopAutoUseTask(Player player)
    {
        PLAYERS.remove(player);
    }


    public void addAutoSkill(Player player, int skillId)
    {
        player.getAutoUseSettings().getAutoSkills().add(skillId);
        startAutoUseTask(player);
    }

    public void removeAutoSkill(Player player, int skillId)
    {
        player.getAutoUseSettings().getAutoSkills().remove(skillId);
        stopAutoUseTask(player);
    }

    public void addAutoSupplyItem(Player player, int itemId)
    {
        player.getAutoUseSettings().getAutoSupplyItems().add(itemId);
        startAutoUseTask(player);
    }

    public void removeAutoSupplyItem(Player player, int itemId)
    {
        player.getAutoUseSettings().getAutoSupplyItems().remove(itemId);
        stopAutoUseTask(player);
    }

    public static AutoUseTaskManager getInstance()
    {
        return SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder
    {
        protected static final AutoUseTaskManager INSTANCE = new AutoUseTaskManager();
    }

}


