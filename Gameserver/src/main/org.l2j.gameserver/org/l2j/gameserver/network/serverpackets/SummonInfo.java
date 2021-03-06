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
import org.l2j.gameserver.enums.NpcInfoType;
import org.l2j.gameserver.enums.Team;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.world.zone.ZoneType;

import java.util.Set;

/**
 * @author Sdw
 */
public class SummonInfo extends AbstractMaskPacket<NpcInfoType> {
    private final Summon _summon;
    private final Player _attacker;
    private final int _val;
    private final byte[] _masks = new byte[]
            {
                    (byte) 0x00,
                    (byte) 0x0C,
                    (byte) 0x0C,
                    (byte) 0x00,
                    (byte) 0x00
            };
    private final String _title;
    private final Set<AbnormalVisualEffect> _abnormalVisualEffects;
    private int _initSize = 0;
    private int _blockSize = 0;
    private int _clanCrest = 0;
    private int _clanLargeCrest = 0;
    private int _allyCrest = 0;
    private int _allyId = 0;
    private int _clanId = 0;
    private int _statusMask = 0;

    public SummonInfo(Summon summon, Player attacker, int val) {
        _summon = summon;
        _attacker = attacker;
        _title = (summon.getOwner() != null) && summon.getOwner().isOnline() ? summon.getOwner().getName() : "";
        _val = val;
        _abnormalVisualEffects = summon.getEffectList().getCurrentAbnormalVisualEffects();

        if (summon.getTemplate().getDisplayId() != summon.getTemplate().getId()) {
            _masks[2] |= 0x10;
            addComponentType(NpcInfoType.NAME);
        }

        addComponentType(NpcInfoType.ATTACKABLE, NpcInfoType.RELATIONS, NpcInfoType.TITLE, NpcInfoType.ID, NpcInfoType.POSITION, NpcInfoType.ALIVE, NpcInfoType.RUNNING, NpcInfoType.PVP_FLAG);

        if (summon.getHeading() > 0) {
            addComponentType(NpcInfoType.HEADING);
        }

        if ((summon.getStats().getPAtkSpd() > 0) || (summon.getStats().getMAtkSpd() > 0)) {
            addComponentType(NpcInfoType.ATK_CAST_SPEED);
        }

        if (summon.getRunSpeed() > 0) {
            addComponentType(NpcInfoType.SPEED_MULTIPLIER);
        }

        if ((summon.getWeapon() > 0) || (summon.getArmor() > 0)) {
            addComponentType(NpcInfoType.EQUIPPED);
        }

        if (summon.getTeam() != Team.NONE) {
            addComponentType(NpcInfoType.TEAM);
        }

        if (summon.isInsideZone(ZoneType.WATER) || summon.isFlying()) {
            addComponentType(NpcInfoType.SWIM_OR_FLY);
        }

        if (summon.isFlying()) {
            addComponentType(NpcInfoType.FLYING);
        }

        if (summon.getMaxHp() > 0) {
            addComponentType(NpcInfoType.MAX_HP);
        }

        if (summon.getMaxMp() > 0) {
            addComponentType(NpcInfoType.MAX_MP);
        }

        if (summon.getCurrentHp() <= summon.getMaxHp()) {
            addComponentType(NpcInfoType.CURRENT_HP);
        }

        if (summon.getCurrentMp() <= summon.getMaxMp()) {
            addComponentType(NpcInfoType.CURRENT_MP);
        }

        if (!_abnormalVisualEffects.isEmpty()) {
            addComponentType(NpcInfoType.ABNORMALS);
        }

        if (summon.getTemplate().getWeaponEnchant() > 0) {
            addComponentType(NpcInfoType.ENCHANT);
        }

        if (summon.getTransformationDisplayId() > 0) {
            addComponentType(NpcInfoType.TRANSFORMATION);
        }

        if (summon.isShowSummonAnimation()) {
            addComponentType(NpcInfoType.SUMMONED);
        }

        if (summon.getReputation() != 0) {
            addComponentType(NpcInfoType.REPUTATION);
        }

        if (summon.getOwner().getClan() != null) {
            _clanId = summon.getOwner().getAppearance().getVisibleClanId();
            _clanCrest = summon.getOwner().getAppearance().getVisibleClanCrestId();
            _clanLargeCrest = summon.getOwner().getAppearance().getVisibleClanLargeCrestId();
            _allyCrest = summon.getOwner().getAppearance().getVisibleAllyId();
            _allyId = summon.getOwner().getAppearance().getVisibleAllyCrestId();

            addComponentType(NpcInfoType.CLAN);
        }

        addComponentType(NpcInfoType.COLOR_EFFECT);

        // TODO: Confirm me
        if (summon.isInCombat()) {
            _statusMask |= 0x01;
        }
        if (summon.isDead()) {
            _statusMask |= 0x02;
        }
        if (summon.isTargetable()) {
            _statusMask |= 0x04;
        }

        _statusMask |= 0x08;

        // Show red aura?
        // if (_statusMask != 0)
        // {
        // addComponentType(NpcInfoType.VISUAL_STATE);
        // }
    }

    @Override
    protected byte[] getMasks() {
        return _masks;
    }

    @Override
    protected void onNewMaskAdded(NpcInfoType component) {
        calcBlockSize(_summon, component);
    }

    private void calcBlockSize(Summon summon, NpcInfoType type) {
        switch (type) {
            case ATTACKABLE:
            case RELATIONS: {
                _initSize += type.getBlockLength();
                break;
            }
            case TITLE: {
                _initSize += type.getBlockLength() + (_title.length() * 2);
                break;
            }
            case NAME: {
                _blockSize += type.getBlockLength() + (summon.getName().length() * 2);
                break;
            }
            default: {
                _blockSize += type.getBlockLength();
                break;
            }
        }
    }

    @Override
    public void writeImpl(GameClient client, WritableBuffer buffer) {
        writeId(ServerPacketId.SUMMON_INFO, buffer );

        buffer.writeInt(_summon.getObjectId());
        buffer.writeByte(_val); // 0=teleported 1=default 2=summoned
        buffer.writeShort(37); // mask_bits_37
        buffer.writeBytes(_masks);

        // Block 1
        buffer.writeByte(_initSize);

        if (containsMask(NpcInfoType.ATTACKABLE)) {
            buffer.writeByte(_summon.isAutoAttackable(_attacker));
        }
        if (containsMask(NpcInfoType.RELATIONS)) {
            buffer.writeInt(_summon.getOwner().getRelation(_attacker));
        }
        if (containsMask(NpcInfoType.TITLE)) {
            buffer.writeString(_title);
        }

        // Block 2
        buffer.writeShort(_blockSize);
        if (containsMask(NpcInfoType.ID)) {
            buffer.writeInt(_summon.getTemplate().getDisplayId() + 1000000);
        }
        if (containsMask(NpcInfoType.POSITION)) {
            buffer.writeInt(_summon.getX());
            buffer.writeInt(_summon.getY());
            buffer.writeInt(_summon.getZ());
        }
        if (containsMask(NpcInfoType.HEADING)) {
            buffer.writeInt(_summon.getHeading());
        }
        if (containsMask(NpcInfoType.UNKNOWN2)) {
            buffer.writeInt(0x00); // Unknown
        }
        if (containsMask(NpcInfoType.ATK_CAST_SPEED)) {
            buffer.writeInt(_summon.getPAtkSpd());
            buffer.writeInt(_summon.getMAtkSpd());
        }
        if (containsMask(NpcInfoType.SPEED_MULTIPLIER)) {
            buffer.writeFloat((float) _summon.getStats().getMovementSpeedMultiplier());
            buffer.writeFloat((float) _summon.getStats().getAttackSpeedMultiplier());
        }
        if (containsMask(NpcInfoType.EQUIPPED)) {
            buffer.writeInt(_summon.getWeapon());
            buffer.writeInt(_summon.getArmor()); // Armor id?
            buffer.writeInt(0x00);
        }
        if (containsMask(NpcInfoType.ALIVE)) {
            buffer.writeByte(!_summon.isDead());
        }
        if (containsMask(NpcInfoType.RUNNING)) {
            buffer.writeByte(_summon.isRunning());
        }
        if (containsMask(NpcInfoType.SWIM_OR_FLY)) {
            buffer.writeByte((_summon.isInsideZone(ZoneType.WATER) ? 0x01 : _summon.isFlying() ? 0x02 : 0x00));
        }
        if (containsMask(NpcInfoType.TEAM)) {
            buffer.writeByte(_summon.getTeam().getId());
        }
        if (containsMask(NpcInfoType.ENCHANT)) {
            buffer.writeInt(_summon.getTemplate().getWeaponEnchant());
        }
        if (containsMask(NpcInfoType.FLYING)) {
            buffer.writeInt(_summon.isFlying());
        }
        if (containsMask(NpcInfoType.CLONE)) {
            buffer.writeInt(0x00); // Player ObjectId with Decoy
        }
        if (containsMask(NpcInfoType.COLOR_EFFECT)) {
            // No visual effect
            buffer.writeInt(0x00); // Unknown
        }
        if (containsMask(NpcInfoType.DISPLAY_EFFECT)) {
            buffer.writeInt(0x00);
        }
        if (containsMask(NpcInfoType.TRANSFORMATION)) {
            buffer.writeInt(_summon.getTransformationDisplayId()); // Transformation ID
        }
        if (containsMask(NpcInfoType.CURRENT_HP)) {
            buffer.writeInt((int) _summon.getCurrentHp());
        }
        if (containsMask(NpcInfoType.CURRENT_MP)) {
            buffer.writeInt((int) _summon.getCurrentMp());
        }
        if (containsMask(NpcInfoType.MAX_HP)) {
            buffer.writeInt(_summon.getMaxHp());
        }
        if (containsMask(NpcInfoType.MAX_MP)) {
            buffer.writeInt(_summon.getMaxMp());
        }
        if (containsMask(NpcInfoType.SUMMONED)) {
            buffer.writeByte((_summon.isShowSummonAnimation() ? 0x02 : 0x00)); // 2 - do some animation on spawn
        }
        if (containsMask(NpcInfoType.UNKNOWN12)) {
            buffer.writeInt(0x00);
            buffer.writeInt(0x00);
        }
        if (containsMask(NpcInfoType.NAME)) {
            buffer.writeString(_summon.getName());
        }
        if (containsMask(NpcInfoType.NAME_NPCSTRINGID)) {
            buffer.writeInt(-1); // NPCStringId for name
        }
        if (containsMask(NpcInfoType.TITLE_NPCSTRINGID)) {
            buffer.writeInt(-1); // NPCStringId for title
        }
        if (containsMask(NpcInfoType.PVP_FLAG)) {
            buffer.writeByte(_summon.getPvpFlag()); // PVP flag
        }
        if (containsMask(NpcInfoType.REPUTATION)) {
            buffer.writeInt(_summon.getReputation()); // Name color
        }
        if (containsMask(NpcInfoType.CLAN)) {
            buffer.writeInt(_clanId);
            buffer.writeInt(_clanCrest);
            buffer.writeInt(_clanLargeCrest);
            buffer.writeInt(_allyId);
            buffer.writeInt(_allyCrest);
        }

        if (containsMask(NpcInfoType.VISUAL_STATE)) {
            buffer.writeByte(_statusMask);
        }

        if (containsMask(NpcInfoType.ABNORMALS)) {
            buffer.writeShort(_abnormalVisualEffects.size());
            for (AbnormalVisualEffect abnormalVisualEffect : _abnormalVisualEffects) {
                buffer.writeShort(abnormalVisualEffect.getClientId());
            }
        }
    }

}