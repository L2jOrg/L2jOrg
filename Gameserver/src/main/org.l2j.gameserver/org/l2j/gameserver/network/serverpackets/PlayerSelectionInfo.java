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

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.LevelData;
import org.l2j.gameserver.enums.InventorySlot;
import org.l2j.gameserver.model.PlayerSelectInfo;
import org.l2j.gameserver.model.entity.Hero;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.settings.ServerSettings;

import java.util.List;

import static org.l2j.commons.configuration.Configurator.getSettings;
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
        this.maxPlayerAllowed = getSettings(ServerSettings.class).maxPlayersAllowed();
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.CHARACTER_SELECTION_INFO);

        final int size = playersInfo.size();
        writeInt(size); // Created character count


        writeInt(maxPlayerAllowed); // Can prevent players from creating new characters (if 0); (if 1, the client will ask if chars may be created (0x13) Response: (0x0D) )
        writeByte(size == maxPlayerAllowed); // if 1 can't create new char
        writeByte(0x01); // 0=can't play, 1=can play free until level 85, 2=100% free play
        writeInt(0x02); // if 1, Korean client
        writeByte(0x00); // Gift message for inactive accounts // 152
        writeByte(0x00); // Balthus Knights, if 1 suggests premium account

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
            writeString(data.getName());
            writeInt(data.getCharId());
            writeString(account);
            writeInt(sessionId);
            writeInt(data.getClanId()); // Pledge ID
            writeInt(data.getAccessLevel()); // Builder level

            writeInt(data.isFemale());
            writeInt(data.getRace());
            writeInt(data.getClassId());

            writeInt(getSettings(ServerSettings.class).serverId());

            writeInt(data.getX());
            writeInt(data.getY());
            writeInt(data.getZ());
            writeDouble(data.getHp());
            writeDouble(data.getMp());

            writeLong(data.getSp());
            writeLong(data.getExp());
            writeDouble((float) (data.getExp() - LevelData.getInstance().getExpForLevel(data.getLevel())) / (LevelData.getInstance().getExpForLevel(data.getLevel() + 1) - LevelData.getInstance().getExpForLevel(data.getLevel())));

            writeInt(data.getLevel());

            writeInt(data.getReputation());
            writeInt(data.getPk());
            writeInt(data.getPvP());

            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);
            writeInt(0x00);

            writeInt(0x00); // Ertheia
            writeInt(0x00); // Ertheia

            for (var slot : getPaperdollOrder()) {
                writeInt(playerInfo.getPaperdollItemId(slot));
            }

            writeInt(0x00); // RHAND Visual ID not Used on Classic
            writeInt(0x00); // LHAND Visual ID not Used on Classic
            writeInt(0x00); // GLOVES Visual ID not Used on Classic
            writeInt(0x00); // CHEST Visual ID not Used on Classic
            writeInt(0x00); // LEGS Visual ID not Used on Classic
            writeInt(0x00); // FEET Visual ID not Used on Classic
            writeInt(0x00); // RHAND Visual ID not Used on Classic
            writeInt(0x00); // HAIR Visual ID not Used on Classic
            writeInt(0x00); // HAIR2 Visual ID not Used on Classic

            writeShort( playerInfo.getEnchantEffect(InventorySlot.CHEST));
            writeShort( playerInfo.getEnchantEffect(InventorySlot.LEGS));
            writeShort( playerInfo.getEnchantEffect(InventorySlot.HEAD));
            writeShort( playerInfo.getEnchantEffect(InventorySlot.GLOVES));
            writeShort( playerInfo.getEnchantEffect(InventorySlot.FEET));

            writeInt(playerInfo.getHairStyle());
            writeInt(playerInfo.getHairColor());
            writeInt(playerInfo.getFace());

            writeDouble(data.getMaxtHp());
            writeDouble(data.getMaxtMp());

            writeInt(data.getDeleteTime() > 0 ? (int) ((data.getDeleteTime() - System.currentTimeMillis()) / 1000) : 0);
            writeInt(data.getClassId());
            writeInt(i == activeSlot); // selected

            writeByte(Math.min(playerInfo.getEnchantEffect(RIGHT_HAND), 127));
            writeInt(playerInfo.getAugmentationOption1());
            writeInt(playerInfo.getAugmentationOption2());

            writeInt(0x00); // Currently on retail when you are on character select you don't see your transformation.

            writeInt(0x00); // Pet NpcId
            writeInt(0x00); // Pet level
            writeInt(0x00); // Pet Food
            writeInt(0x00); // Pet Food Level
            writeDouble(0x00); // Current pet HP
            writeDouble(0x00); // Current pet MP

            writeInt(data.getVitalityPoints());
            writeInt((int) (Config.RATE_VITALITY_EXP_MULTIPLIER * 100)); // Vitality Percent
            writeInt(playerInfo.getVitalityItemsUsed()); // Remaining vitality item uses
            writeInt(data.getAccessLevel() != -100); // Char is active or not
            writeByte(data.isNobless());
            writeByte(Hero.getInstance().isHero(data.getCharId()) ? 0x02 : 0x00); // Hero glow
            writeByte(playerInfo.isHairAccessoryEnabled()); // Show hair accessory if enabled
            writeInt(playerInfo.getRemainBanExpireTime()); // ban time in secs
            writeInt((int) (playerInfo.getLastAccess() / 1000));

        }
    }
}
