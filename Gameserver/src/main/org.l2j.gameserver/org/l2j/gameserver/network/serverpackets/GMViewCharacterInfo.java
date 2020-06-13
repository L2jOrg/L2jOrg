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
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.GM_VIEW_CHARACTER_INFO);

        writeInt(_activeChar.getX());
        writeInt(_activeChar.getY());
        writeInt(_activeChar.getZ());
        writeInt(_activeChar.getHeading());
        writeInt(_activeChar.getObjectId());
        writeString(_activeChar.getName());
        writeInt(_activeChar.getRace().ordinal());
        writeInt(_activeChar.getAppearance().isFemale() ? 1 : 0);
        writeInt(_activeChar.getClassId().getId());
        writeInt(_activeChar.getLevel());
        writeLong(_activeChar.getExp());
        writeDouble((float) (_activeChar.getExp() - LevelData.getInstance().getExpForLevel(_activeChar.getLevel())) / (LevelData.getInstance().getExpForLevel(_activeChar.getLevel() + 1) - LevelData.getInstance().getExpForLevel(_activeChar.getLevel()))); // High Five exp %
        writeInt(_activeChar.getSTR());
        writeInt(_activeChar.getDEX());
        writeInt(_activeChar.getCON());
        writeInt(_activeChar.getINT());
        writeInt(_activeChar.getWIT());
        writeInt(_activeChar.getMEN());
        writeInt(0x00); // LUC
        writeInt(0x00); // CHA
        writeInt(_activeChar.getMaxHp());
        writeInt((int) _activeChar.getCurrentHp());
        writeInt(_activeChar.getMaxMp());
        writeInt((int) _activeChar.getCurrentMp());
        writeLong(_activeChar.getSp());
        writeInt(_activeChar.getCurrentLoad());
        writeInt(_activeChar.getMaxLoad());
        writeInt(_activeChar.getPkKills());

        for (var slot : getPaperdollOrder()) {
            writeInt(_activeChar.getInventory().getPaperdollObjectId(slot));
        }

        for (var slot : getPaperdollOrder()) {
            writeInt(_activeChar.getInventory().getPaperdollItemDisplayId(slot));
        }

        for (var slot : getPaperdollOrder()) { // TODO review
            final VariationInstance augment = _activeChar.getInventory().getPaperdollAugmentation(slot);
            writeInt(augment != null ? augment.getOption1Id() : 0); // Confirmed
            writeInt(augment != null ? augment.getOption2Id() : 0); // Confirmed
        }

        writeByte( _activeChar.getInventory().getTalismanSlots()); // CT2.3
        writeByte(_activeChar.getInventory().canEquipCloak()); // CT2.3
        writeInt(0x00);
        writeShort(0x00);
        writeInt(_activeChar.getPAtk());
        writeInt(_activeChar.getPAtkSpd());
        writeInt(_activeChar.getPDef());
        writeInt(_activeChar.getEvasionRate());
        writeInt(_activeChar.getAccuracy());
        writeInt(_activeChar.getCriticalHit());
        writeInt(_activeChar.getMAtk());

        writeInt(_activeChar.getMAtkSpd());
        writeInt(_activeChar.getPAtkSpd());

        writeInt(_activeChar.getMDef());
        writeInt(_activeChar.getMagicEvasionRate());
        writeInt(_activeChar.getMagicAccuracy());
        writeInt(_activeChar.getMCriticalHit());

        writeInt(_activeChar.getPvpFlag()); // 0-non-pvp 1-pvp = violett name
        writeInt(_activeChar.getReputation());

        writeInt(_runSpd);
        writeInt(_walkSpd);
        writeInt(_swimRunSpd);
        writeInt(_swimWalkSpd);
        writeInt(_flyRunSpd);
        writeInt(_flyWalkSpd);
        writeInt(_flyRunSpd);
        writeInt(_flyWalkSpd);
        writeDouble(_moveMultiplier);
        writeDouble(_activeChar.getAttackSpeedMultiplier()); // 2.9);//
        writeDouble(_activeChar.getCollisionRadius()); // scale
        writeDouble(_activeChar.getCollisionHeight()); // y offset ??!? fem dwarf 4033
        writeInt(_activeChar.getAppearance().getHairStyle());
        writeInt(_activeChar.getAppearance().getHairColor());
        writeInt(_activeChar.getAppearance().getFace());
        writeInt(_activeChar.isGM() ? 0x01 : 0x00); // builder level

        writeString(_activeChar.getTitle());
        writeInt(_activeChar.getClanId()); // pledge id
        writeInt(_activeChar.getClanCrestId()); // pledge crest id
        writeInt(_activeChar.getAllyId()); // ally id
        writeByte((byte) _activeChar.getMountType().ordinal()); // mount type
        writeByte((byte) _activeChar.getPrivateStoreType().getId());
        writeByte((byte)(_activeChar.hasDwarvenCraft() ? 1 : 0));
        writeInt(_activeChar.getPkKills());
        writeInt(_activeChar.getPvpKills());

        writeShort((short) _activeChar.getRecomLeft());
        writeShort((short) _activeChar.getRecomHave()); // Blue value for name (0 = white, 255 = pure blue)
        writeInt(_activeChar.getClassId().getId());
        writeInt(0x00); // special effects? circles around player...
        writeInt(_activeChar.getMaxCp());
        writeInt((int) _activeChar.getCurrentCp());

        writeByte((byte)(_activeChar.isRunning() ? 0x01 : 0x00)); // changes the Speed display on Status Window

        writeByte((byte) 321);

        writeInt(_activeChar.getPledgeClass()); // changes the text above CP on Status Window

        writeByte((byte) (_activeChar.isNoble() ? 0x01 : 0x00));
        writeByte((byte) (_activeChar.isHero() ? 0x01 : 0x00));

        writeInt(_activeChar.getAppearance().getNameColor());
        writeInt(_activeChar.getAppearance().getTitleColor());

        final AttributeType attackAttribute = _activeChar.getAttackElement();
        writeShort((short) attackAttribute.getClientId());
        writeShort((short) _activeChar.getAttackElementValue(attackAttribute));
        for (AttributeType type : AttributeType.ATTRIBUTE_TYPES) {
            writeShort((short) _activeChar.getDefenseElementValue(type));
        }
        writeInt(_activeChar.getFame());
        writeInt(_activeChar.getVitalityPoints());
        writeInt(0x00);
        writeInt(0x00);
    }

}
