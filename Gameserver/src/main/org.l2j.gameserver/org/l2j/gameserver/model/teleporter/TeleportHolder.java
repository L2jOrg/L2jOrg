package org.l2j.gameserver.model.teleporter;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.datatables.ItemTable;
import org.l2j.gameserver.enums.SpecialItemType;
import org.l2j.gameserver.enums.TeleportType;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.StatsSet;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.events.EventDispatcher;
import org.l2j.gameserver.model.events.impl.character.npc.OnNpcTeleportRequest;
import org.l2j.gameserver.model.events.returns.TerminateReturn;
import org.l2j.gameserver.model.items.CommonItem;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * Teleport holder
 *
 * @author UnAfraid
 */
public final class TeleportHolder {
    private static final Logger LOGGER = LoggerFactory.getLogger(TeleportHolder.class);

    private final String _name;
    private final TeleportType _type;
    private final List<TeleportLocation> _teleportData = new ArrayList<>();

    /**
     * Constructor
     *
     * @param name name of teleport list
     * @param type type of teleport list
     */
    public TeleportHolder(String name, TeleportType type) {
        _name = name;
        _type = type;
    }

    /**
     * Gets list identification (name).
     *
     * @return list name
     */
    public String getName() {
        return _name;
    }

    /**
     * Check if teleport list is for noblesse or not.
     *
     * @return {@code true} if is for noblesse otherwise {@code false}
     */
    public boolean isNoblesse() {
        return (_type == TeleportType.NOBLES_ADENA) || (_type == TeleportType.NOBLES_TOKEN);
    }

    /**
     * Gets type of teleport list.
     *
     * @return type of list
     */
    public TeleportType getType() {
        return _type;
    }

    /**
     * Create new teleport location in this holder.
     *
     * @param locData information about teleport location
     */
    public void registerLocation(StatsSet locData) {
        _teleportData.add(new TeleportLocation(_teleportData.size(), locData));
    }

    /**
     * Gets teleport location with specific index.
     *
     * @param locationId index of location (begins with {@code 0})
     * @return instance of {@link TeleportLocation} if found otherwise {@code null}
     */
    public TeleportLocation getLocation(int locationId) {
        return _teleportData.get(locationId);
    }

    /**
     * Gets all teleport locations registered in current holder.
     *
     * @return collection of {@link TeleportLocation}
     */
    public List<TeleportLocation> getLocations() {
        return _teleportData;
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
            LOGGER.warn("Player " + player.getObjectId() + " requested noblesse teleport without being noble!");
            return;
        }

        // Load variables
        final int questZoneId = isNormalTeleport() ? player.getQuestZoneId() : -1;

        // Build html
        final StringBuilder sb = new StringBuilder();
        final StringBuilder sb_f = new StringBuilder();
        for (TeleportLocation loc : _teleportData) {
            String finalName = loc.getName();
            String confirmDesc = loc.getName();
            if (loc.getNpcStringId() != null) {
                final int stringId = loc.getNpcStringId().getId();
                finalName = "<fstring>" + stringId + "</fstring>";
                confirmDesc = "F;" + stringId;
            }

            if (shouldPayFee(player, loc)) {
                final long fee = calculateFee(player, loc);
                if (fee != 0) {
                    finalName += " - " + fee + " " + getItemName(loc.getFeeId(), true);
                }
            }

            final boolean isQuestTeleport = (questZoneId >= 0) && (loc.getQuestZoneId() == questZoneId);
            if (isQuestTeleport) {
                sb_f.append("<button align=left icon=\"quest\" action=\"bypass -h " + bypass + " " + _name + " " + loc.getId() + "\" msg=\"811;" + confirmDesc + "\">" + finalName + "</button>");
            } else {
                sb.append("<button align=left icon=\"teleport\" action=\"bypass -h " + bypass + " " + _name + " " + loc.getId() + "\" msg=\"811;" + confirmDesc + "\">" + finalName + "</button>");
            }
        }
        sb_f.append(sb.toString());

        // Send html message
        final NpcHtmlMessage msg = new NpcHtmlMessage(npc.getObjectId());
        msg.setFile(player, "data/html/teleporter/teleports.htm");
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
            LOGGER.warn("Player " + player.getObjectId() + " requested noblesse teleport without being noble!");
            return;
        }

        final TeleportLocation loc = getLocation(locId);
        if (loc == null) {
            LOGGER.warn("Player " + player.getObjectId() + " requested unknown teleport location " + locId + " within list " + _name + "!");
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
                msg.setFile(player, "data/html/teleporter/castleteleporter-busy.htm");
                player.sendPacket(msg);
                return;
            } else if (!Config.ALT_GAME_KARMA_PLAYER_CAN_USE_GK && (player.getReputation() < 0)) {
                player.sendMessage("Go away, you're not welcome here.");
                return;
            } else if (player.isCombatFlagEquipped()) {
                player.sendPacket(SystemMessageId.YOU_CANNOT_TELEPORT_WHILE_IN_POSSESSION_OF_A_WARD);
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
                player.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_ENOUGH_ADENA);
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

            final Calendar cal = Calendar.getInstance();
            final int hour = cal.get(Calendar.HOUR_OF_DAY);
            final int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
            if ((hour >= 20) && ((dayOfWeek >= Calendar.MONDAY) && (dayOfWeek <= Calendar.TUESDAY))) {
                return loc.getFeeCount() / 2;
            }
        }
        return loc.getFeeCount();
    }

    private boolean isNormalTeleport()
    {
        return (_type == TeleportType.NORMAL) || (_type == TeleportType.HUNTING);
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
                return "<fstring>1000308</fstring>";
            } else if (itemId == CommonItem.ANCIENT_ADENA) {
                return "<fstring>1000309</fstring>";
            }
        }
        final ItemTemplate item = ItemTable.getInstance().getTemplate(itemId);
        if (item != null) {
            return item.getName();
        }

        final SpecialItemType specialItem = SpecialItemType.getByClientId(itemId);
        if (specialItem != null) {
            switch (specialItem) {
                case PC_CAFE_POINTS: {
                    return "Player Commendation Points";
                }
                case CLAN_REPUTATION: {
                    return "Clan Reputation Points";
                }
                case FAME: {
                    return "Fame";
                }
                case FIELD_CYCLE_POINTS: {
                    return "Field Cycle Points";
                }
                case RAIDBOSS_POINTS: {
                    return "Raid Points";
                }
            }
        }
        return "Unknown item: " + itemId;
    }
}
