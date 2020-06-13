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
package org.l2j.gameserver.model.teleporter;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.item.ItemEngine;
import org.l2j.gameserver.enums.SpecialItemType;
import org.l2j.gameserver.enums.TeleportType;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcTeleportRequest;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.item.CommonItem;
import org.l2j.gameserver.model.item.ItemTemplate;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.html.NpcHtmlMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import static java.util.Objects.isNull;
import static org.l2j.commons.util.Util.SPACE;

/**
 * Teleport holder
 *
 * @author UnAfraid
 * @author joeAlisson
 */
public final class TeleportHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeleportHolder.class);

    private static final String NPC_STRING_ID_FORMAT = "<fstring>%d</fstring>";
    private static final String BUTTON_QUEST_BYPASS = "<button align=left icon=\"quest\" action=\"bypass -h ";
    private static final String BUTTON_TELEPORT_BYPASS = "<button align=left icon=\"teleport\" action=\"bypass -h ";
    private static final String CONFIRM_TELEPORT_MSG = "\" msg=\"811;";
    private static final String ADENA_STRING_ID = "<fstring>1000308</fstring>";
    private static final String ANCIENT_ADENA_STRING_ID = "<fstring>1000309</fstring>";
    private static final String TEMPLATE_TELEPORTER_HTM = "data/html/teleporter/teleports.htm";
    private static final String CASTLE_TELEPORTER_BUSY_HTM = "data/html/teleporter/castleteleporter-busy.htm";
    private static final EnumSet<DayOfWeek> DISCOUNT_DAYS = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY);

    private final String name;
    private final TeleportType type;
    private final List<TeleportLocation> teleportData = new ArrayList<>();

    /**
     * Constructor
     *
     * @param name name of teleport list
     * @param type type of teleport list
     */
    public TeleportHolder(String name, TeleportType type) {
        this.name = name;
        this.type = type;
    }

    /**
     * Create new teleport location in this holder.
     *
     * @param locData information about teleport location
     */
    public void registerLocation(StatsSet locData) {
        teleportData.add(new TeleportLocation(teleportData.size(), locData));
    }

    /**
     * Build HTML message from teleport list and send it to player.
     *
     * @param player receiver of HTML message
     * @param npc    teleporter
     */
    public void showTeleportList(Player player, Npc npc) {
        showTeleportList(player, npc, "npc_" + npc.getObjectId() + "_teleport");
    }

    /**
     * Build HTML message from teleport list and send it to player.
     *
     * @param player receiver of HTML message
     * @param npc    teleporter
     * @param bypass bypass used while building message
     */
    public void showTeleportList(Player player, Npc npc, String bypass) {
        if (isNoblesse() && !player.isNoble()) {
            LOGGER.warn("Player {} requested noblesse teleport without being noble!", player.getObjectId());
            return;
        }

        // Load variables
        final int questZoneId = isNormalTeleport() ? player.getQuestZoneId() : -1;

        // Build html
        final StringBuilder sb = new StringBuilder();
        final StringBuilder sb_f = new StringBuilder();
        for (TeleportLocation loc : teleportData) {
            String finalName = loc.getName();
            String confirmDesc = loc.getName();
            if (loc.getNpcStringId() != -1) {
                finalName = String.format(NPC_STRING_ID_FORMAT, loc.getNpcStringId());
                confirmDesc = "F;" + loc.getNpcStringId();
            }

            if (shouldPayFee(player, loc)) {
                final long fee = calculateFee(player, loc);
                if (fee != 0) {
                    finalName += " - " + fee + SPACE + getItemName(loc.getFeeId(), true);
                }
            }

            final boolean isQuestTeleport = (questZoneId >= 0) && (loc.getQuestZoneId() == questZoneId);
            var builder = isQuestTeleport  ?  sb_f.append(BUTTON_QUEST_BYPASS) : sb.append(BUTTON_TELEPORT_BYPASS);
            builder.append(bypass).append(SPACE).append(name).append(SPACE).append(loc.getId()).append(CONFIRM_TELEPORT_MSG).append(confirmDesc).append("\">")
                    .append(finalName).append("</button>");
        }
        sb_f.append(sb.toString());

        // Send html message
        final NpcHtmlMessage msg = new NpcHtmlMessage(npc.getObjectId());
        msg.setFile(player, TEMPLATE_TELEPORTER_HTM);
        msg.replace("%locations%", sb_f.toString());
        player.sendPacket(msg);
    }

    /**
     * Teleports player to final location
     *
     * @param player player being teleported
     * @param npc    teleporter
     * @param locId  destination
     */
    public void doTeleport(Player player, Npc npc, int locId) {
        if (isNoblesse() && !player.isNoble()) {
            LOGGER.warn("Player {} requested noblesse teleport without being noble!", player.getObjectId());
            return;
        }

        final TeleportLocation loc = getLocation(locId);
        if (isNull(loc)) {
            LOGGER.warn("Player {} requested unknown teleport location {} within list {}!", player.getObjectId(), locId, name);
            return;
        }

        // Check if castle is in siege
        for (int castleId : loc.getCastleId()) {
            if (CastleManager.getInstance().getCastleById(castleId).getSiege().isInProgress()) {
                player.sendPacket(SystemMessageId.YOU_CANNOT_TELEPORT_TO_A_VILLAGE_THAT_IS_IN_A_SIEGE);
                return;
            }
        }

        // Validate conditions for NORMAL teleport
        if (isNormalTeleport()) {
            if (npc.getCastle().getSiege().isInProgress()) {
                final NpcHtmlMessage msg = new NpcHtmlMessage(npc.getObjectId());
                msg.setFile(player, CASTLE_TELEPORTER_BUSY_HTM);
                player.sendPacket(msg);
                return;
            } else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_GK && (player.getReputation() < 0)) {
                player.sendMessage("Go away, you're not welcome here.");
                return;
            }
        }

        // Notify listeners
        final TerminateReturn term = EventDispatcher.getInstance().notifyEvent(new OnNpcTeleportRequest(player, npc, loc), npc, TerminateReturn.class);
        if ((term != null) && term.terminate()) {
            return;
        }

        // Check rest of conditions
        if (shouldPayFee(player, loc) && !player.destroyItemByItemId("Teleport", loc.getFeeId(), calculateFee(player, loc), npc, true)) {
            if (loc.getFeeId() == CommonItem.ADENA) {
                player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA_POPUP);
            } else {
                player.sendMessage("You do not have enough " + getItemName(loc.getFeeId(), false));
            }
        } else if (!player.isAlikeDead()) {
            player.teleToLocation(loc);
        }
    }

    /**
     * Check if player have to play fee or not.
     *
     * @param player player which request teleport
     * @param loc    location where player should be teleported
     * @return {@code true} when all requirements are met otherwise {@code false}
     */
    private boolean shouldPayFee(Player player, TeleportLocation loc) {
        return !isNormalTeleport() || (((player.getLevel() > Config.MAX_FREE_TELEPORT_LEVEL) || player.isSubClassActive()) && ((loc.getFeeId() != 0) && (loc.getFeeCount() > 0)));
    }

    /**
     * Calculate fee amount for requested teleport.<br>
     * For characters below level 77 teleport service is free.<br>
     * From 8.00 pm to 00.00 from Monday till Tuesday for all characters there's a 50% discount on teleportation services
     *
     * @param player player which request teleport
     * @param loc    location where player should be teleported
     * @return fee amount
     */
    private long calculateFee(Player player, TeleportLocation loc) {
        if (isNormalTeleport()) {
            if (!player.isSubClassActive() && (player.getLevel() <= Config.MAX_FREE_TELEPORT_LEVEL)) {
                return 0;
            }

            var now = LocalDateTime.now();
            if(now.getHour() >= 20 && DISCOUNT_DAYS.contains(now.getDayOfWeek())) {
                return loc.getFeeCount() / 2;
            }
        }
        return loc.getFeeCount();
    }

    private boolean isNormalTeleport()
    {
        return (type == TeleportType.NORMAL) || (type == TeleportType.HUNTING);
    }

    /**
     * Gets name of specified item.
     *
     * @param itemId  template id of item
     * @param fstring prefer using client strings
     * @return item name
     */
    private String getItemName(int itemId, boolean fstring) {
        if (fstring) {
            if (itemId == CommonItem.ADENA) {
                return ADENA_STRING_ID;
            } else if (itemId == CommonItem.ANCIENT_ADENA) {
                return ANCIENT_ADENA_STRING_ID;
            }
        }
        final ItemTemplate item = ItemEngine.getInstance().getTemplate(itemId);
        if (item != null) {
            return item.getName();
        }

        final SpecialItemType specialItem = SpecialItemType.getByClientId(itemId);
        if (specialItem != null) {
            return specialItem.getDescription();
        }
        return "Unknown item: " + itemId;
    }

    /**
     * Gets teleport location with specific index.
     *
     * @param locationId index of location (begins with {@code 0})
     * @return instance of {@link TeleportLocation} if found otherwise {@code null}
     */
    public TeleportLocation getLocation(int locationId) {
        return teleportData.get(locationId);
    }

    /**
     * Gets all teleport locations registered in current holder.
     *
     * @return collection of {@link TeleportLocation}
     */
    public List<TeleportLocation> getLocations() {
        return teleportData;
    }

    /**
     * Gets list identification (name).
     *
     * @return list name
     */
    public String getName() {
        return name;
    }

    /**
     * Check if teleport list is for noblesse or not.
     *
     * @return {@code true} if is for noblesse otherwise {@code false}
     */
    public boolean isNoblesse() {
        return (type == TeleportType.NOBLES_ADENA) || (type == TeleportType.NOBLES_TOKEN);
    }

    /**
     * Gets type of teleport list.
     *
     * @return type of list
     */
    public TeleportType getType() {
        return type;
    }
}
