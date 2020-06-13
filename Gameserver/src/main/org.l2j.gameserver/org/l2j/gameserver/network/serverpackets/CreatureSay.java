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
package org.l2j.gameserver.network.serverpackets;

import org.l2j.commons.util.Util;
import org.l2j.gameserver.enums.ChatType;
import org.l2j.gameserver.instancemanager.MentorManager;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.ArrayList;
import java.util.List;

import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public final class CreatureSay extends ServerPacket {

    private final int objectId;
    private final ChatType type;
    private String senderName;
    private String message = null;
    private int npcString = -1;
    private int _mask;
    private int charLevel = -1;
    private List<String> npcStringParameters;
    private int rank;
    private int castleId;

    public CreatureSay(Player sender, Player receiver, String name, ChatType type, String text) {
        this(sender, type, text);

        senderName = name;
        charLevel = sender.getLevel();
        rank = sender.getRank();
        castleId = Util.zeroIfNullOrElse(sender.getClan(), Clan::getCastleId);

        if (nonNull(receiver)) {
            if (receiver.getFriendList().contains(sender.getObjectId())) {
                _mask |= 0x01;
            }
            if ((receiver.getClanId() > 0) && (receiver.getClanId() == sender.getClanId())) {
                _mask |= 0x02;
            }
            if ((MentorManager.getInstance().getMentee(receiver.getObjectId(), sender.getObjectId()) != null) || (MentorManager.getInstance().getMentee(sender.getObjectId(), receiver.getObjectId()) != null)) {
                _mask |= 0x04;
            }
            if ((receiver.getAllyId() > 0) && (receiver.getAllyId() == sender.getAllyId())) {
                _mask |= 0x08;
            }
        }

        // Does not shows level
        if (sender.isGM()) {
            _mask |= 0x10;
        }
    }

    public CreatureSay(Player player, ChatType type, String message) {
        this(player.getObjectId(), type, player.getAppearance().getVisibleName(), message);
        this.rank = player.getRank();
    }

    public CreatureSay(int objectId, ChatType type, String senderName, String text) {
        this.objectId = objectId;
        this.type = type;
        this.senderName = senderName;
        message = text;
    }

    public CreatureSay(int objectId, ChatType messageType, String charName, NpcStringId npcString) {
        this.objectId = objectId;
        type = messageType;
        senderName = charName;
        this.npcString = npcString.getId();
    }

    /**
     * String parameter for argument S1,S2,.. in npcstring-e.dat
     */
    public void addStringParameter(String text) {
        if (npcStringParameters == null) {
            npcStringParameters = new ArrayList<>();
        }
        npcStringParameters.add(text);
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.SAY2);

        writeInt(objectId);
        writeInt(type.getClientId());
        writeString(senderName);
        writeInt(npcString); // High Five NPCString ID

        if (nonNull(message)) {
            writeString(message);
            if ((charLevel > 0) && (type == ChatType.WHISPER)) {
                writeByte(_mask);
                if ((_mask & 0x10) == 0) {
                    writeByte(charLevel);
                }
            }
        } else if (nonNull(npcStringParameters)) {
            for (String s : npcStringParameters) {
                writeString(s);
            }
        }
        writeByte(rank);
        writeByte(castleId);
        writeInt(0x00); // share location
    }

    @Override
    public final void runImpl(Player player) {
        if (player != null) {
            player.broadcastSnoop(type, senderName, message);
        }
    }
}
