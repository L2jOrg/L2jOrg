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
package org.l2j.gameserver.model.olympiad;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.enums.CategoryType;
import org.l2j.gameserver.instancemanager.AntiFeedManager;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.SystemMessage;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author DS
 */
public class OlympiadManager {
    private final Set<Integer> _nonClassBasedRegisters = ConcurrentHashMap.newKeySet();
    private final Map<Integer, Set<Integer>> _classBasedRegisters = new ConcurrentHashMap<>();

    private OlympiadManager() {
    }

    public final Set<Integer> getRegisteredNonClassBased() {
        return _nonClassBasedRegisters;
    }

    public final Map<Integer, Set<Integer>> getRegisteredClassBased() {
        return _classBasedRegisters;
    }

    protected final List<Set<Integer>> hasEnoughRegisteredClassed() {
        List<Set<Integer>> result = null;
        for (Map.Entry<Integer, Set<Integer>> classList : _classBasedRegisters.entrySet()) {
            if ((classList.getValue() != null) && (classList.getValue().size() >= Config.ALT_OLY_CLASSED)) {
                if (result == null) {
                    result = new ArrayList<>();
                }

                result.add(classList.getValue());
            }
        }
        return result;
    }

    protected final boolean hasEnoughRegisteredNonClassed() {
        return _nonClassBasedRegisters.size() >= Config.ALT_OLY_NONCLASSED;
    }

    protected final void clearRegistered() {
        _nonClassBasedRegisters.clear();
        _classBasedRegisters.clear();
        AntiFeedManager.getInstance().clear(AntiFeedManager.OLYMPIAD_ID);
    }

    public final boolean isRegistered(Player noble) {
        return isRegistered(noble, noble, false);
    }

    private boolean isRegistered(Player noble, Player player, boolean showMessage) {
        final int objId =noble.getObjectId();
        if (_nonClassBasedRegisters.contains(objId)) {
            if (showMessage) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ALREADY_REGISTERED_ON_THE_WAITING_LIST_FOR_THE_ALL_CLASS_BATTLE);
                sm.addPcName(noble);
                player.sendPacket(sm);
            }
            return true;
        }

        final Set<Integer> classed = _classBasedRegisters.get(getClassGroup(noble));
        if ((classed != null) && classed.contains(objId)) {
            if (showMessage) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ALREADY_REGISTERED_ON_THE_CLASS_MATCH_WAITING_LIST);
                sm.addPcName(noble);
                player.sendPacket(sm);
            }
            return true;
        }

        return false;
    }

    public final boolean isRegisteredInComp(Player noble) {
        return isRegistered(noble, noble, false) || isInCompetition(noble, noble, false);
    }

    private boolean isInCompetition(Player noble, Player player, boolean showMessage) {
        if (!Olympiad._inCompPeriod) {
            return false;
        }

        AbstractOlympiadGame game;
        for (int i = OlympiadGameManager.getInstance().getNumberOfStadiums(); --i >= 0; ) {
            game = OlympiadGameManager.getInstance().getOlympiadTask(i).getGame();
            if (game == null) {
                continue;
            }

            if (game.containsParticipant(noble.getObjectId())) {
                if (!showMessage) {
                    return true;
                }

                switch (game.getType()) {
                    case CLASSED: {
                        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ALREADY_REGISTERED_ON_THE_CLASS_MATCH_WAITING_LIST);
                        sm.addPcName(noble);
                        player.sendPacket(sm);
                        break;
                    }
                    case NON_CLASSED: {
                        final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_IS_ALREADY_REGISTERED_ON_THE_WAITING_LIST_FOR_THE_ALL_CLASS_BATTLE);
                        sm.addPcName(noble);
                        player.sendPacket(sm);
                        break;
                    }
                }
                return true;
            }
        }
        return false;
    }

    public final boolean registerNoble(Player player, CompetitionType type) {
        if (!Olympiad._inCompPeriod) {
            player.sendPacket(SystemMessageId.THE_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
            return false;
        }

		if (Olympiad.getInstance().getMillisToCompEnd() < 1200000)
		{
            player.sendPacket(SystemMessageId.PARTICIPATION_REQUESTS_ARE_NO_LONGER_BEING_ACCEPTED);
            return false;
        }

        final int charId = player.getObjectId();
        if (Olympiad.getInstance().getRemainingWeeklyMatches(charId) < 1) {
            player.sendPacket(SystemMessageId.YOU_CAN_PARTICIPATE_IN_UP_TO_30_MATCHES_PER_WEEK);
            return false;
        }

        switch (type) {
            case CLASSED: {
                if (player.isOnEvent()) {
                    player.sendMessage("You can't join olympiad while participating on an Event.");
                    return false;
                }

                if ((Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP > 0) && !AntiFeedManager.getInstance().tryAddPlayer(AntiFeedManager.OLYMPIAD_ID, player, Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP)) {
                    final NpcHtmlMessage message = new NpcHtmlMessage(player.getLastHtmlActionOriginId());
                    message.setFile(player, "data/html/mods/OlympiadIPRestriction.htm");
                    message.replace("%max%", String.valueOf(AntiFeedManager.getInstance().getLimit(player, Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP)));
                    player.sendPacket(message);
                    return false;
                }

                _classBasedRegisters.computeIfAbsent(getClassGroup(player), k -> ConcurrentHashMap.newKeySet()).add(charId);
                player.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REGISTERED_FOR_THE_OLYMPIAD_WAITING_LIST_FOR_A_CLASS_BATTLE);
                break;
            }
            case NON_CLASSED: {
                if (player.isOnEvent()) {
                    player.sendMessage("You can't join olympiad while participating on TvT Event.");
                    return false;
                }

                if ((Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP > 0) && !AntiFeedManager.getInstance().tryAddPlayer(AntiFeedManager.OLYMPIAD_ID, player, Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP)) {
                    final NpcHtmlMessage message = new NpcHtmlMessage(player.getLastHtmlActionOriginId());
                    message.setFile(player, "data/html/mods/OlympiadIPRestriction.htm");
                    message.replace("%max%", String.valueOf(AntiFeedManager.getInstance().getLimit(player, Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP)));
                    player.sendPacket(message);
                    return false;
                }

                _nonClassBasedRegisters.add(charId);
                player.sendPacket(SystemMessageId.YOU_VE_BEEN_REGISTERED_IN_THE_WAITING_LIST_OF_ALL_CLASS_BATTLE);
                break;
            }
        }
        return true;
    }

    public final boolean unRegisterNoble(Player noble) {
        if (!Olympiad._inCompPeriod) {
            noble.sendPacket(SystemMessageId.THE_OLYMPIAD_GAMES_ARE_NOT_CURRENTLY_IN_PROGRESS);
            return false;
        }

        if ((!noble.isInCategory(CategoryType.THIRD_CLASS_GROUP) && !noble.isInCategory(CategoryType.FOURTH_CLASS_GROUP)) || (noble.getLevel() < 55)) // Classic noble equivalent check.
        {
            final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.C1_DOES_NOT_MEET_THE_PARTICIPATION_REQUIREMENTS_ONLY_CHARACTERS_THAT_COMPLETED_THE_2ND_CLASS_TRANSFER_CAN_PARTICIPATE_IN_THE_OLYMPIAD);
            sm.addString(noble.getName());
            noble.sendPacket(sm);
            return false;
        }

        if (!isRegistered(noble, noble, false)) {
            noble.sendPacket(SystemMessageId.YOU_ARE_NOT_CURRENTLY_REGISTERED_FOR_THE_OLYMPIAD);
            return false;
        }

        if (isInCompetition(noble, noble, false)) {
            return false;
        }

        final int objId = noble.getObjectId();
        if (_nonClassBasedRegisters.remove(objId)) {
            if (Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP > 0) {
                AntiFeedManager.getInstance().removePlayer(AntiFeedManager.OLYMPIAD_ID, noble);
            }

            noble.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REMOVED_FROM_THE_OLYMPIAD_WAITING_LIST);
            return true;
        }

        final Set<Integer> classed = _classBasedRegisters.get(getClassGroup(noble));
        if ((classed != null) && classed.remove(objId)) {
            if (Config.DUALBOX_CHECK_MAX_OLYMPIAD_PARTICIPANTS_PER_IP > 0) {
                AntiFeedManager.getInstance().removePlayer(AntiFeedManager.OLYMPIAD_ID, noble);
            }

            noble.sendPacket(SystemMessageId.YOU_HAVE_BEEN_REMOVED_FROM_THE_OLYMPIAD_WAITING_LIST);
            return true;
        }

        return false;
    }

    public final void removeDisconnectedCompetitor(Player player) {
        final OlympiadGameTask task = OlympiadGameManager.getInstance().getOlympiadTask(player.getOlympiadGameId());
        if ((task != null) && task.isGameStarted()) {
            task.getGame().handleDisconnect(player);
        }

        final int objId = player.getObjectId();
        if (_nonClassBasedRegisters.remove(objId)) {
            return;
        }

        _classBasedRegisters.getOrDefault(getClassGroup(player), Collections.emptySet()).remove(objId);
    }

    public int getCountOpponents() {
        return _nonClassBasedRegisters.size() + _classBasedRegisters.size();
    }

    private int getClassGroup(Player player) {
        return player.getBaseClass();
    }

    public static OlympiadManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final OlympiadManager INSTANCE = new OlympiadManager();
    }
}
