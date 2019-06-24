/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.data.xml.impl.TeleportersData;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.enums.TeleportType;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.model.actor.templates.L2NpcTemplate;
import org.l2j.gameserver.model.teleporter.TeleportHolder;
import org.l2j.gameserver.network.serverpackets.NpcHtmlMessage;
import org.l2j.gameserver.util.GameUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.StringTokenizer;


/**
 * @author NightMarez
 */
public final class L2TeleporterInstance extends L2Npc {
    private static final Logger LOGGER = LoggerFactory.getLogger(L2TeleporterInstance.class);

    public L2TeleporterInstance(L2NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2TeleporterInstance);
    }

    @Override
    public boolean isAutoAttackable(L2Character attacker) {
        return attacker.isMonster() || super.isAutoAttackable(attacker);
    }

    @Override
    public void onBypassFeedback(L2PcInstance player, String command) {
        // Process bypass
        final StringTokenizer st = new StringTokenizer(command, " ");
        switch (st.nextToken()) {
            case "showNoblesSelect": {
                sendHtmlMessage(player, "data/html/teleporter/" + (player.isNoble() ? "nobles_select" : "not_nobles") + ".htm");
                break;
            }
            case "showTeleports": {
                final String listName = (st.hasMoreTokens()) ? st.nextToken() : TeleportType.NORMAL.name();
                final TeleportHolder holder = TeleportersData.getInstance().getHolder(getId(), listName);
                if (holder == null) {
                    LOGGER.warn("Player " + player.getObjectId() + " requested show teleports for list with name " + listName + " at NPC " + getId() + "!");
                    return;
                }
                holder.showTeleportList(player, this);
                break;
            }
            case "showTeleportsHunting":
            {
                final String listName = (st.hasMoreTokens()) ? st.nextToken() : TeleportType.HUNTING.name();
                final TeleportHolder holder = TeleportersData.getInstance().getHolder(getId(), listName);
                if (holder == null)
                {
                    LOGGER.warn("Player {} requested show teleports for hunting list with name {}  at NPC {}!", player.getObjectId(), listName, getId());
                    return;
                }
                holder.showTeleportList(player, this);
                break;
            }
            case "teleport": {
                // Check for required count of params.
                if (st.countTokens() != 2) {
                    LOGGER.warn("Player " + player.getObjectId() + " send unhandled teleport command: " + command);
                    return;
                }

                final String listName = st.nextToken();
                final TeleportHolder holder = TeleportersData.getInstance().getHolder(getId(), listName);
                if (holder == null) {
                    LOGGER.warn("Player " + player.getObjectId() + " requested unknown teleport list: " + listName + " for npc: " + getId() + "!");
                    return;
                }
                holder.doTeleport(player, this, parseNextInt(st, -1));
                break;
            }
            case "chat": {
                int val = 0;
                try {
                    val = Integer.parseInt(command.substring(5));
                } catch (IndexOutOfBoundsException | NumberFormatException ignored) {
                }
                showChatWindow(player, val);
                break;
            }
            default: {
                super.onBypassFeedback(player, command);
            }
        }
    }

    private int parseNextInt(StringTokenizer st, int defaultVal) {
        if (st.hasMoreTokens()) {
            final String token = st.nextToken();
            if (GameUtils.isDigit(token)) {
                return Integer.valueOf(token);
            }
        }
        return defaultVal;
    }

    @Override
    public String getHtmlPath(int npcId, int val) {
        final String pom = (val == 0) ? String.valueOf(npcId) : (npcId + "-" + val);
        return "data/html/teleporter/" + pom + ".htm";
    }

    @Override
    public void showChatWindow(L2PcInstance player) {
        // Teleporter isn't on castle ground
        if (CastleManager.getInstance().getCastle(this) == null) {
            super.showChatWindow(player);
            return;
        }

        // Teleporter is on castle ground
        String filename = "data/html/teleporter/castleteleporter-no.htm";
        if ((player.getClan() != null) && (getCastle().getOwnerId() == player.getClanId())) // Clan owns castle
        {
            filename = getHtmlPath(getId(), 0); // Owner message window
        } else if (getCastle().getSiege().isInProgress()) // Teleporter is busy due siege
        {
            filename = "data/html/teleporter/castleteleporter-busy.htm"; // Busy because of siege
        }
        sendHtmlMessage(player, filename);
    }

    private void sendHtmlMessage(L2PcInstance player, String filename) {
        final NpcHtmlMessage html = new NpcHtmlMessage(getObjectId());
        html.setFile(player, filename);
        html.replace("%objectId%", String.valueOf(getObjectId()));
        html.replace("%npcname%", getName());
        player.sendPacket(html);
    }
}
