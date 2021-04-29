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
package org.l2j.gameserver.network.serverpackets;

import io.github.joealisson.mmocore.WritableBuffer;
import org.l2j.gameserver.data.xml.impl.LevelData;
import org.l2j.gameserver.engine.olympiad.Olympiad;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.PlayerSelectInfo;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.settings.RateSettings;
import org.l2j.gameserver.settings.ServerSettings;

import java.util.List;

import static org.l2j.gameserver.enums.InventorySlot.RIGHT_HAND;

/**
 * @author JoeAlisson
 * @author Thoss
 */
public class PlayerSelectionInfo extends ServerPacket {

    private final String account;
    private final int sessionId;
    private final List<PlayerSelectInfo> playersInfo;
    private final int maxPlayerAllowed;
    private int activeSlot;

    public PlayerSelectionInfo(GameClient client) {
        this(client, client.getActiveSlot());
    }

    public PlayerSelectionInfo(GameClient client, int activeSlot) {
        this.sessionId = client.getSessionKey().getGameServerSessionId();
        this.account = client.getAccountName();
        this.playersInfo = client.getPlayersInfo();
        this.activeSlot = activeSlot;
        this.maxPlayerAllowed = ServerSettings.maxPlayersAllowed();
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.CHARACTER_SELECTION_INFO, buffer );

        final int size = playersInfo.size();
        buffer.writeInt(size); // Created character count


        buffer.writeInt(maxPlayerAllowed); // Can prevent players from creating new characters (if 0); (if 1, the client will ask if chars may be created (0x13) Response: (0x0D) )
        buffer.writeByte(size == maxPlayerAllowed); // if 1 can't create new char
        buffer.writeByte(0x01); // 0=can't play, 1=can play free until level 85, 2=100% free play
        buffer.writeInt(0x02); // if 1, Korean client
        buffer.writeByte(0x00); // Gift message for inactive accounts // 152
        buffer.writeByte(0x00); // Balthus Knights, if 1 suggests premium account

        long lastAccess = 0;
        if (activeSlot == -1) {
            for (int i = 0; i < size; i++) {
                if (lastAccess < playersInfo.get(i).getLastAccess()) {
                    lastAccess = playersInfo.get(i).getLastAccess();
                    activeSlot = i;
                }
            }
        }

        for (int i = 0; i < size; i++) {
            var playerInfo = playersInfo.get(i);
            var data = playerInfo.getData();
            buffer.writeString(data.getName());
            buffer.writeInt(data.getCharId());
            buffer.writeString(account);
            buffer.writeInt(sessionId);
            buffer.writeInt(data.getClanId()); // Pledge ID
            buffer.writeInt(data.getAccessLevel()); // Builder level

            buffer.writeInt(data.isFemale());
            buffer.writeInt(data.getRace());
            buffer.writeInt(data.getClassId());

            buffer.writeInt(ServerSettings.serverId());

            buffer.writeInt(data.getX());
            buffer.writeInt(data.getY());
            buffer.writeInt(data.getZ());
            buffer.writeDouble(data.getHp());
            buffer.writeDouble(data.getMp());

            buffer.writeLong(data.getSp());
            buffer.writeLong(data.getExp());
            buffer.writeDouble((float) (data.getExp() - LevelData.getInstance().getExpForLevel(data.getLevel())) / (LevelData.getInstance().getExpForLevel(data.getLevel() + 1) - LevelData.getInstance().getExpForLevel(data.getLevel())));

            buffer.writeInt(data.getLevel());

            buffer.writeInt(data.getReputation());
            buffer.writeInt(data.getPk());
            buffer.writeInt(data.getPvP());

            buffer.writeInt(0x00);
            buffer.writeInt(0x00);
            buffer.writeInt(0x00);
            buffer.writeInt(0x00);
            buffer.writeInt(0x00);
            buffer.writeInt(0x00);
            buffer.writeInt(0x00);

            buffer.writeInt(0x00); // Ertheia
            buffer.writeInt(0x00); // Ertheia

            for (var slot : getPaperdollOrder()) {
                buffer.writeInt(playerInfo.getPaperdollItemId(slot));
            }

            buffer.writeInt(0x00); // RHAND Visual ID not Used on Classic
            buffer.writeInt(0x00); // LHAND Visual ID not Used on Classic
            buffer.writeInt(0x00); // GLOVES Visual ID not Used on Classic
            buffer.writeInt(0x00); // CHEST Visual ID not Used on Classic
            buffer.writeInt(0x00); // LEGS Visual ID not Used on Classic
            buffer.writeInt(0x00); // FEET Visual ID not Used on Classic
            buffer.writeInt(0x00); // RHAND Visual ID not Used on Classic
            buffer.writeInt(0x00); // HAIR Visual ID not Used on Classic
            buffer.writeInt(0x00); // HAIR2 Visual ID not Used on Classic

            buffer.writeShort( playerInfo.getEnchantEffect(InventorySlot.CHEST));
            buffer.writeShort( playerInfo.getEnchantEffect(InventorySlot.LEGS));
            buffer.writeShort( playerInfo.getEnchantEffect(InventorySlot.HEAD));
            buffer.writeShort( playerInfo.getEnchantEffect(InventorySlot.GLOVES));
            buffer.writeShort( playerInfo.getEnchantEffect(InventorySlot.FEET));

            buffer.writeInt(playerInfo.getHairStyle());
            buffer.writeInt(playerInfo.getHairColor());
            buffer.writeInt(playerInfo.getFace());

            buffer.writeDouble(data.getMaxtHp());
            buffer.writeDouble(data.getMaxMp());

            buffer.writeInt(data.getDeleteTime() > 0 ? (int) ((data.getDeleteTime() - System.currentTimeMillis()) / 1000) : 0);
            buffer.writeInt(data.getClassId());
            buffer.writeInt(i == activeSlot); // selected

            buffer.writeByte(Math.min(playerInfo.getEnchantEffect(RIGHT_HAND), 127));
            buffer.writeInt(playerInfo.getAugmentationOption1());
            buffer.writeInt(playerInfo.getAugmentationOption2());

            buffer.writeInt(0x00); // Currently on retail when you are on character select you don't see your transformation.

            buffer.writeInt(0x00); // Pet NpcId
            buffer.writeInt(0x00); // Pet level
            buffer.writeInt(0x00); // Pet Food
            buffer.writeInt(0x00); // Pet Food Level
            buffer.writeDouble(0x00); // Current pet HP
            buffer.writeDouble(0x00); // Current pet MP

            buffer.writeInt(data.getVitalityPoints());
            buffer.writeInt((int)  RateSettings.rateVitalityExpMul() * 100); // Vitality Percent
            buffer.writeInt(playerInfo.getVitalityItemsUsed()); // Remaining vitality item uses
            buffer.writeInt(data.getAccessLevel() != -100); // Char is active or not
            buffer.writeByte(data.isNobless());
            buffer.writeByte(Olympiad.getInstance().isHero(data.getCharId()) ? 0x02 : 0x00); // Hero glow
            buffer.writeByte(playerInfo.isHairAccessoryEnabled()); // Show hair accessory if enabled
            buffer.writeInt(playerInfo.getRemainBanExpireTime()); // ban time in secs
            buffer.writeInt((int) (playerInfo.getLastAccess() / 1000));

        }
    }
}
