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
import org.l2j.gameserver.enums.AttributeType;
import org.l2j.gameserver.model.VariationInstance;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

public class GMViewCharacterInfo extends ServerPacket {
    private final Player _activeChar;
    private final int _runSpd;
    private final int _walkSpd;
    private final int _swimRunSpd;
    private final int _swimWalkSpd;
    private final int _flyRunSpd;
    private final int _flyWalkSpd;
    private final double _moveMultiplier;

    public GMViewCharacterInfo(Player cha) {
        _activeChar = cha;
        _moveMultiplier = cha.getMovementSpeedMultiplier();
        _runSpd = (int) Math.round(cha.getRunSpeed() / _moveMultiplier);
        _walkSpd = (int) Math.round(cha.getWalkSpeed() / _moveMultiplier);
        _swimRunSpd = (int) Math.round(cha.getSwimRunSpeed() / _moveMultiplier);
        _swimWalkSpd = (int) Math.round(cha.getSwimWalkSpeed() / _moveMultiplier);
        _flyRunSpd = cha.isFlying() ? _runSpd : 0;
        _flyWalkSpd = cha.isFlying() ? _walkSpd : 0;
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.GM_VIEW_CHARACTER_INFO, buffer );

        buffer.writeInt(_activeChar.getX());
        buffer.writeInt(_activeChar.getY());
        buffer.writeInt(_activeChar.getZ());
        buffer.writeInt(_activeChar.getHeading());
        buffer.writeInt(_activeChar.getObjectId());
        buffer.writeString(_activeChar.getName());
        buffer.writeInt(_activeChar.getRace().ordinal());
        buffer.writeInt(_activeChar.getAppearance().isFemale() ? 1 : 0);
        buffer.writeInt(_activeChar.getClassId().getId());
        buffer.writeInt(_activeChar.getLevel());
        buffer.writeLong(_activeChar.getExp());
        buffer.writeDouble((float) (_activeChar.getExp() - LevelData.getInstance().getExpForLevel(_activeChar.getLevel())) / (LevelData.getInstance().getExpForLevel(_activeChar.getLevel() + 1) - LevelData.getInstance().getExpForLevel(_activeChar.getLevel()))); // High Five exp %
        buffer.writeInt(_activeChar.getSTR());
        buffer.writeInt(_activeChar.getDEX());
        buffer.writeInt(_activeChar.getCON());
        buffer.writeInt(_activeChar.getINT());
        buffer.writeInt(_activeChar.getWIT());
        buffer.writeInt(_activeChar.getMEN());
        buffer.writeInt(0x00); // LUC
        buffer.writeInt(0x00); // CHA
        buffer.writeInt(_activeChar.getMaxHp());
        buffer.writeInt((int) _activeChar.getCurrentHp());
        buffer.writeInt(_activeChar.getMaxMp());
        buffer.writeInt((int) _activeChar.getCurrentMp());
        buffer.writeLong(_activeChar.getSp());
        buffer.writeInt(_activeChar.getCurrentLoad());
        buffer.writeInt(_activeChar.getMaxLoad());
        buffer.writeInt(_activeChar.getPkKills());

        for (var slot : getPaperdollOrder()) {
            buffer.writeInt(_activeChar.getInventory().getPaperdollObjectId(slot));
        }

        for (var slot : getPaperdollOrder()) {
            buffer.writeInt(_activeChar.getInventory().getPaperdollItemDisplayId(slot));
        }

        for (var slot : getPaperdollOrder()) { // TODO review
            final VariationInstance augment = _activeChar.getInventory().getPaperdollAugmentation(slot);
            buffer.writeInt(augment != null ? augment.getOption1Id() : 0); // Confirmed
            buffer.writeInt(augment != null ? augment.getOption2Id() : 0); // Confirmed
        }

        buffer.writeByte( _activeChar.getInventory().getTalismanSlots()); // CT2.3
        buffer.writeByte(_activeChar.getInventory().canEquipCloak()); // CT2.3
        buffer.writeInt(0x00);
        buffer.writeShort(0x00);
        buffer.writeInt(_activeChar.getPAtk());
        buffer.writeInt(_activeChar.getPAtkSpd());
        buffer.writeInt(_activeChar.getPDef());
        buffer.writeInt(_activeChar.getEvasionRate());
        buffer.writeInt(_activeChar.getAccuracy());
        buffer.writeInt(_activeChar.getCriticalHit());
        buffer.writeInt(_activeChar.getMAtk());

        buffer.writeInt(_activeChar.getMAtkSpd());
        buffer.writeInt(_activeChar.getPAtkSpd());

        buffer.writeInt(_activeChar.getMDef());
        buffer.writeInt(_activeChar.getMagicEvasionRate());
        buffer.writeInt(_activeChar.getMagicAccuracy());
        buffer.writeInt(_activeChar.getMCriticalHit());

        buffer.writeInt(_activeChar.getPvpFlag()); // 0-non-pvp 1-pvp = violett name
        buffer.writeInt(_activeChar.getReputation());

        buffer.writeInt(_runSpd);
        buffer.writeInt(_walkSpd);
        buffer.writeInt(_swimRunSpd);
        buffer.writeInt(_swimWalkSpd);
        buffer.writeInt(_flyRunSpd);
        buffer.writeInt(_flyWalkSpd);
        buffer.writeInt(_flyRunSpd);
        buffer.writeInt(_flyWalkSpd);
        buffer.writeDouble(_moveMultiplier);
        buffer.writeDouble(_activeChar.getAttackSpeedMultiplier()); // 2.9);//
        buffer.writeDouble(_activeChar.getCollisionRadius()); // scale
        buffer.writeDouble(_activeChar.getCollisionHeight()); // y offset ??!? fem dwarf 4033
        buffer.writeInt(_activeChar.getAppearance().getHairStyle());
        buffer.writeInt(_activeChar.getAppearance().getHairColor());
        buffer.writeInt(_activeChar.getAppearance().getFace());
        buffer.writeInt(_activeChar.isGM() ? 0x01 : 0x00); // builder level

        buffer.writeString(_activeChar.getTitle());
        buffer.writeInt(_activeChar.getClanId()); // pledge id
        buffer.writeInt(_activeChar.getClanCrestId()); // pledge crest id
        buffer.writeInt(_activeChar.getAllyId()); // ally id
        buffer.writeByte(_activeChar.getMountType().ordinal()); // mount type
        buffer.writeByte(_activeChar.getPrivateStoreType().getId());
        buffer.writeByte(_activeChar.hasDwarvenCraft());
        buffer.writeInt(_activeChar.getPkKills());
        buffer.writeInt(_activeChar.getPvpKills());

        buffer.writeShort(_activeChar.getRecommendLeft());
        buffer.writeShort(_activeChar.getRecommend()); // Blue value for name (0 = white, 255 = pure blue)
        buffer.writeInt(_activeChar.getClassId().getId());
        buffer.writeInt(0x00); // special effects? circles around player...
        buffer.writeInt(_activeChar.getMaxCp());
        buffer.writeInt((int) _activeChar.getCurrentCp());

        buffer.writeByte(_activeChar.isRunning()); // changes the Speed display on Status Window

        buffer.writeByte(321);

        buffer.writeInt(_activeChar.getPledgeClass()); // changes the text above CP on Status Window

        buffer.writeByte(_activeChar.isNoble());
        buffer.writeByte(_activeChar.isHero());

        buffer.writeInt(_activeChar.getAppearance().getNameColor());
        buffer.writeInt(_activeChar.getAppearance().getTitleColor());

        final AttributeType attackAttribute = _activeChar.getAttackElement();
        buffer.writeShort(attackAttribute.getClientId());
        buffer.writeShort(_activeChar.getAttackElementValue(attackAttribute));
        for (AttributeType type : AttributeType.ATTRIBUTE_TYPES) {
            buffer.writeShort(_activeChar.getDefenseElementValue(type));
        }
        buffer.writeInt(_activeChar.getFame());
        buffer.writeInt(_activeChar.getVitalityPoints());
        buffer.writeInt(0x00);
        buffer.writeInt(0x00);
    }

}
