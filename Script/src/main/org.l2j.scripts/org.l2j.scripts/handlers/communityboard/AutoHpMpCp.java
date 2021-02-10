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
package org.l2j.scripts.handlers.communityboard;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.cache.HtmCache;
import org.l2j.gameserver.engine.item.Item;
import org.l2j.gameserver.handler.IItemHandler;
import org.l2j.gameserver.handler.IParseBoardHandler;
import org.l2j.gameserver.handler.ItemHandler;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventType;
import org.l2j.gameserver.model.events.ListenerRegisterType;
import org.l2j.gameserver.model.events.annotations.RegisterEvent;
import org.l2j.gameserver.model.events.annotations.RegisterType;
import org.l2j.gameserver.model.events.impl.character.player.OnPlayerLogout;
import org.l2j.gameserver.model.item.EtcItem;
import org.l2j.gameserver.world.World;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.nonNull;

/**
 * @author Thoss
 **/
public class AutoHpMpCp implements IParseBoardHandler {
    private static final Logger LOGGER = LoggerFactory.getLogger(AutoHpMpCp.class);
    private static final String[] CMD =  new String[] { "_bbsautohpmpcp" };

    private static final int AUTO_POTION_INTERVAL = 1000;
    private static final Map<String, List<Integer>> potionsList = Map.of(
            "autohp", Config.AUTO_HP_ITEM_IDS,
            "automp", Config.AUTO_MP_ITEM_IDS,
            "autocp", Config.AUTO_CP_ITEM_IDS);

    private static final ForkJoinPool autoPotionPool = new ForkJoinPool();
    private static Map<Integer, List<String>> listenedPlayer = new ConcurrentHashMap<>();

    private static final DoAutoPotion doAutoPotion = new DoAutoPotion();
    private static ScheduledFuture<?> autoPotionTask;


    private AutoHpMpCp() {
        autoPotionTask = ThreadPool.scheduleAtFixedDelay(doAutoPotion, AUTO_POTION_INTERVAL, AUTO_POTION_INTERVAL);
    }

    @RegisterEvent(EventType.ON_PLAYER_LOGIN)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void OnPlayerLogin(OnPlayerLogout event)
    {
        final Player activeChar = event.getPlayer();

        final int autoHp = activeChar.getAutoHp();
        final int autoMp = activeChar.getAutoMp();
        final int autoCp = activeChar.getAutoCp();

        if(autoHp > 0) executeCommand(activeChar, "autohp", autoCp);
        if(autoMp > 0) executeCommand(activeChar, "automp", autoCp);
        if(autoCp > 0) executeCommand(activeChar, "autocp", autoCp);
    }

    @RegisterEvent(EventType.ON_PLAYER_LOGOUT)
    @RegisterType(ListenerRegisterType.GLOBAL_PLAYERS)
    public void OnPlayerLogout(OnPlayerLogout event)
    {
        final int playerOID = event.getPlayer().getObjectId();
        if (listenedPlayer.containsKey(playerOID)) {
            listenedPlayer.remove(playerOID);
        }
    }

    @Override
    public boolean parseCommunityBoardCommand(String command, StringTokenizer tokens, Player player) {
        String subCommand = tokens.nextToken();

        if(subCommand.equalsIgnoreCase("autocp")) {
            processCommand(player, "autocp", tokens);
        }
        else if(subCommand.equalsIgnoreCase("autohp")) {
            processCommand(player, "autohp", tokens);
        }
        else if(subCommand.equalsIgnoreCase("automp")) {
            processCommand(player, "automp", tokens);
        }

        final String customPath = Config.CUSTOM_CB_ENABLED ? "Custom/" : "";
        HtmCache.getInstance().getHtm(player, "data/html/CommunityBoard/" + customPath + "home.html");
        return true;
    }

    private static String processCommand(Player activeChar, String command, StringTokenizer params) {
        try {
            int percent = Math.min(99, Integer.parseInt(params.nextToken()));

            if(percent < 0) {
                activeChar.sendMessage("You can not specify a negative value!");
                return "";
            }

            return executeCommand(activeChar, command,percent);

        } catch(NumberFormatException e) {
            activeChar.sendMessage("Incorrect number");
            return "";
        }
    }

    private static String executeCommand(Player activeChar, String command, int percent) {
        List<String> listenedKeys = listenedPlayer.get(activeChar.getObjectId());

        if(percent > 0) {
            setPercentByCommand(activeChar, command, percent);

            if (listenedKeys == null)
                listenedPlayer.put(activeChar.getObjectId(), new ArrayList<>());

            listenedPlayer.get(activeChar.getObjectId()).add(command);
            activeChar.sendMessage("You have enabled " + command + " recovery. You will automatically recover at a value of " + percent + "% or less.");
        } else {
            setPercentByCommand(activeChar, command, percent);

            if (listenedKeys == null) return "";

            if(listenedPlayer.get(activeChar.getObjectId()).size() > 1)
                listenedPlayer.get(activeChar.getObjectId()).remove(command);
            else
                listenedPlayer.remove(activeChar.getObjectId());

            activeChar.sendMessage(command + " system disabled.");
        }

        return "";
    }

    private static int getPercentByCommand(Player activeChar, String command) {
        switch (command) {
            case "autocp" -> { return activeChar.getAutoCp(); }
            case "autohp" -> { return activeChar.getAutoHp(); }
            case "automp" -> { return activeChar.getAutoMp(); }
        }

        return 0;
    }

    private static void setPercentByCommand(Player activeChar, String command, int percent)  {
        switch (command) {
            case "autocp" -> activeChar.setAutoCp(percent);
            case "autohp" -> activeChar.setAutoHp(percent);
            case "automp" -> activeChar.setAutoMp(percent);
        }
    }

    private static final class DoAutoPotion implements Runnable {

        @Override
        public void run() {
            autoPotionPool.submit(this::useAutoPotion);
        }

        private void useAutoPotion() {
            for (Map.Entry<Integer, List<String>> playerInfo : listenedPlayer.entrySet()) {
                Player player = World.getInstance().findPlayer(playerInfo.getKey());

                if(player == null) {
                    listenedPlayer.remove(playerInfo.getKey());
                    return;
                }

                playerInfo.getValue().forEach(command -> {
                    if(canUseAutoPotion(player, command)) {
                        boolean success = false;
                        for (int itemId : potionsList.get(command)) {
                            final Item potion = player.getInventory().getItemByItemId(itemId);
                            if ((potion != null) && (potion.getCount() > 0)) {
                                success = true;
                                int reuseDelay = potion.getReuseDelay();
                                if (reuseDelay <= 0 || player.getItemRemainingReuseTime(potion.getObjectId()) <= 0) {
                                    EtcItem etcItem = potion.getEtcItem();
                                    IItemHandler handler = ItemHandler.getInstance().getHandler(etcItem);

                                    if (nonNull(handler) && handler.useItem(player, potion, false)) {
                                        player.onActionRequest();
                                        if(reuseDelay > 0) {
                                            player.addTimeStampItem(potion, reuseDelay);
                                        }
                                    }
                                }
                                player.sendMessage(command + " potion: Restored.");
                                break;
                            }
                        }
                        if (!success) {
                            player.sendMessage(command + " potion: You are out of potions!");
                            listenedPlayer.get(playerInfo.getKey()).remove(command);
                        }
                    }
                });

            }
        }

        private boolean canUseAutoPotion(Player player, String command) {
            return canUseAutoPlay(player) && getPercentByCommand(player, command) >= player.getCurrentHpPercent();
        }

        private boolean canUseAutoPlay(Player player) {
            return  player.getAI().getIntention() != CtrlIntention.AI_INTENTION_PICK_UP &&
                    player.getAI().getIntention() != CtrlIntention.AI_INTENTION_CAST &&
                    !player.hasBlockActions() &&
                    !player.isControlBlocked() &&
                    !player.isAlikeDead() &&
                    !player.isInObserverMode() &&
                    !player.isCastingNow();
        }
    }

    public void shutdown() {
        autoPotionPool.shutdown();
        autoPotionTask.cancel(true);
        autoPotionTask = null;
    }

    public static IParseBoardHandler provider() {
        return new AutoHpMpCp();
    }

    @Override
    public String[] getCommunityBoardCommands() {
        return CMD;
    }
}

/*
*
* code below as a reminder for system picking up item "auto" in player inventory
* Might cause bad performance because iterating over whole inventory so use with care
*
*
*
*
Item effectedItem = null;
int effectedItemPower = 0;

Item instantItem = null;
int instantItemPower = 0;

final EffectList playerEffects = player.getEffectList();
loop: for(Item item : player.getInventory().getItems())
{
    ItemSkillHolder skillEntry = item.getTemplate().getAllSkills().get(0);
    if(skillEntry == null)
        continue;

    Skill skill = skillEntry.getSkill();
    for(AbstractEffect et : skill.getEffects(EffectScope.GENERAL))
    {
        if(et.getEffectType() == EffectType.MANAHEAL_BY_LEVEL || et.getEffectType() == EffectType.MANAHEAL_PERCENT)
        {
            for(BuffInfo effect : playerEffects.getEffects())
            {
                if(effect.getSkill() == skill)
                {
                    // Dot not apply potion if another one already healing
                    effectedItem = null;
                    effectedItemPower = 0;
                    break loop;
                }
            }

            if(!ItemFunctions.checkForceUseItem(player, item, false) || !ItemFunctions.checkUseItem(player, item, false))
                continue loop;

            int power = (int) et.getValue();
            if(power > effectedItemPower)
            {
                if(skill.checkCondition(player, player, false, false, true, false, false))
                {
                    effectedItem = item;
                    effectedItemPower = power;
                    continue loop;
                }
            }
        }
    }
}

loop: for(ItemInstance item : player.getInventory().getItems())
{
    SkillEntry skillEntry = item.getTemplate().getFirstSkill();
    if(skillEntry == null)
        continue;

    if(!ItemFunctions.checkForceUseItem(player, item, false) || !ItemFunctions.checkUseItem(player, item, false))
        continue;

    Skill skill = skillEntry.getTemplate();
    for(EffectTemplate et : skill.getEffectTemplates(EffectUseType.NORMAL_INSTANT))
    {
        if(et.getEffectType() == EffectType.RestoreMP)
        {
            int power = (int) et.getValue();
            if(et.getParam().getBool("percent", false))
                power = power * (int) (player.getMaxMp() / 100.);
            if(power > instantItemPower)
            {
                if(skill.checkCondition(player, player, false, false, true, false, false))
                {
                    instantItem = item;
                    instantItemPower = power;
                    continue loop;
                }
            }
        }
    }
}

if(instantItem != null)
    useItem(player, instantItem);

if(effectedItem != null)
{
    if(instantItemPower == 0 || percent >= (newMp + instantItemPower) / (player.getMaxMp() / 100.))
        useItem(player, effectedItem);
}

private static class ChangeCurrentCpListener extends OnPlayerCpChange {
    public ChangeCurrentCpListener(Player activeChar) {
        super(activeChar);
    }

    public void OnPlayerCpChange(Player player)
    {
        if(player.isDead())
            return;

        int percent = player.getVarInt("autocp", 0);
        int currentPercent = (int) (newCp / (player.getMaxCp() / 100.));
        if(percent <= 0 || currentPercent <= 0 || currentPercent > percent)
            return;

        ItemInstance effectedItem = null;
        int effectedItemPower = 0;

        ItemInstance instantItem = null;
        int instantItemPower = 0;

        final Collection<Abnormal> abnormals = player.getAbnormalList().values();
        loop: for(ItemInstance item : player.getInventory().getItems())
        {
            SkillEntry skillEntry = item.getTemplate().getFirstSkill();
            if(skillEntry == null)
                continue;

            Skill skill = skillEntry.getTemplate();
            for(EffectTemplate et : skill.getEffectTemplates(EffectUseType.NORMAL))
            {
                if(et.getEffectType() == EffectType.RestoreCP)
                {
                    for(Abnormal abnormal : abnormals)
                    {
                        if(abnormal.getSkill() == skill)
                        {
                            for(Effect effect : abnormal.getEffects())
                            {
                                if(effect.getEffectType() == EffectType.RestoreCP)
                                {
                                    // Не хиляем, если уже наложена какая-либо хилка.
                                    effectedItem = null;
                                    effectedItemPower = 0;
                                    break loop;
                                }
                            }
                        }
                    }

                    if(!ItemFunctions.checkForceUseItem(player, item, false) || !ItemFunctions.checkUseItem(player, item, false))
                        continue loop;

                    int power = (int) et.getValue();
                    if(power > effectedItemPower)
                    {
                        if(skill.checkCondition(player, player, false, false, true, false, false))
                        {
                            effectedItem = item;
                            effectedItemPower = power;
                            continue loop;
                        }
                    }
                }
            }
        }

        loop: for(ItemInstance item : player.getInventory().getItems())
        {
            SkillEntry skillEntry = item.getTemplate().getFirstSkill();
            if(skillEntry == null)
                continue;

            if(!ItemFunctions.checkForceUseItem(player, item, false) || !ItemFunctions.checkUseItem(player, item, false))
                continue;

            Skill skill = skillEntry.getTemplate();
            for(EffectTemplate et : skill.getEffectTemplates(EffectUseType.NORMAL_INSTANT))
            {
                if(et.getEffectType() == EffectType.RestoreCP)
                {
                    int power = (int) et.getValue();
                    if(et.getParam().getBool("percent", false))
                        power = power * (int) (player.getMaxCp() / 100.);
                    if(power > instantItemPower)
                    {
                        if(skill.checkCondition(player, player, false, false, true, false, false))
                        {
                            instantItem = item;
                            instantItemPower = power;
                            continue loop;
                        }
                    }
                }
            }
        }

        if(instantItem != null)
            useItem(player, instantItem);

        if(effectedItem != null)
        {
            if(instantItemPower == 0 || percent >= (newCp + instantItemPower) / (player.getMaxCp() / 100.))
                useItem(player, effectedItem);
        }
    }
}*/