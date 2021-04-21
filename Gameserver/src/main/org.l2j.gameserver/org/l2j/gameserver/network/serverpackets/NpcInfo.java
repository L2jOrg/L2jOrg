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
import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.clan.ClanEngine;
import org.l2j.gameserver.enums.NpcInfoType;
import org.l2j.gameserver.enums.Team;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Guard;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.NpcStringId;
import org.l2j.gameserver.network.ServerPacketId;
import org.l2j.gameserver.world.zone.ZoneType;

import java.util.Set;

import static org.l2j.gameserver.util.GameUtils.*;

/**
 * @author UnAfraid
 */
public class NpcInfo extends AbstractMaskPacket<NpcInfoType> {
    private final Npc _npc;
    private final byte[] _masks = new byte[]
            {
                    (byte) 0x00,
                    (byte) 0x0C,
                    (byte) 0x0C,
                    (byte) 0x00,
                    (byte) 0x00
            };
    private final Set<AbnormalVisualEffect> _abnormalVisualEffects;
    private int _initSize = 0;
    private int _blockSize = 0;
    private int _clanCrest = 0;
    private int _clanLargeCrest = 0;
    private int _allyCrest = 0;
    private int _allyId = 0;
    private int _clanId = 0;
    private int _statusMask = 0;

    public NpcInfo(Npc npc) {
        _npc = npc;
        _abnormalVisualEffects = npc.getEffectList().getCurrentAbnormalVisualEffects();

        addComponentType(NpcInfoType.ATTACKABLE, NpcInfoType.RELATIONS, NpcInfoType.TITLE, NpcInfoType.ID, NpcInfoType.POSITION, NpcInfoType.ALIVE, NpcInfoType.RUNNING);

        if (npc.getHeading() > 0) {
            addComponentType(NpcInfoType.HEADING);
        }

        if ((npc.getStats().getPAtkSpd() > 0) || (npc.getStats().getMAtkSpd() > 0)) {
            addComponentType(NpcInfoType.ATK_CAST_SPEED);
        }

        if (npc.getRunSpeed() > 0) {
            addComponentType(NpcInfoType.SPEED_MULTIPLIER);
        }

        if ((npc.getLeftHandItem() > 0) || (npc.getRightHandItem() > 0)) {
            addComponentType(NpcInfoType.EQUIPPED);
        }

        if (npc.getTeam() != Team.NONE) {
            addComponentType(NpcInfoType.TEAM);
        }

        if (npc.getDisplayEffect() > 0) {
            addComponentType(NpcInfoType.DISPLAY_EFFECT);
        }

        if (npc.isInsideZone(ZoneType.WATER) || npc.isFlying()) {
            addComponentType(NpcInfoType.SWIM_OR_FLY);
        }

        if (npc.isFlying()) {
            addComponentType(NpcInfoType.FLYING);
        }

        if (npc.getCloneObjId() > 0) {
            addComponentType(NpcInfoType.CLONE);
        }

        if (npc.getMaxHp() > 0) {
            addComponentType(NpcInfoType.MAX_HP);
        }

        if (npc.getMaxMp() > 0) {
            addComponentType(NpcInfoType.MAX_MP);
        }

        if (npc.getCurrentHp() <= npc.getMaxHp()) {
            addComponentType(NpcInfoType.CURRENT_HP);
        }

        if (npc.getCurrentMp() <= npc.getMaxMp()) {
            addComponentType(NpcInfoType.CURRENT_MP);
        }

        if (npc.getTemplate().isUsingServerSideName()) {
            addComponentType(NpcInfoType.NAME);
        }

        if (npc.getTemplate().isUsingServerSideTitle() || (Config.SHOW_NPC_LVL && isMonster(npc)) || npc.isChampion() || isTrap(npc)) {
            addComponentType(NpcInfoType.TITLE);
        }

        if (npc.getNameString() != null) {
            addComponentType(NpcInfoType.NAME_NPCSTRINGID);
        }

        if (npc.getTitleString() != null) {
            addComponentType(NpcInfoType.TITLE_NPCSTRINGID);
        }

        if (_npc.getReputation() != 0) {
            addComponentType(NpcInfoType.REPUTATION);
        }

        if (!_abnormalVisualEffects.isEmpty() || npc.isInvisible()) {
            addComponentType(NpcInfoType.ABNORMALS);
        }

        if (npc.getEnchantEffect() > 0) {
            addComponentType(NpcInfoType.ENCHANT);
        }

        if (npc.getTransformationDisplayId() > 0) {
            addComponentType(NpcInfoType.TRANSFORMATION);
        }

        if (npc.isShowSummonAnimation()) {
            addComponentType(NpcInfoType.SUMMONED);
        }

        if (npc.getClanId() > 0) {
            final Clan clan = ClanEngine.getInstance().getClan(npc.getClanId());
            if (clan != null) {
                _clanId = clan.getId();
                _clanCrest = clan.getCrestId();
                _clanLargeCrest = clan.getCrestLargeId();
                _allyCrest = clan.getAllyCrestId();
                _allyId = clan.getAllyId();

                addComponentType(NpcInfoType.CLAN);
            }
        }

        addComponentType(NpcInfoType.COLOR_EFFECT);

        if (npc.getPvpFlag() > 0) {
            addComponentType(NpcInfoType.PVP_FLAG);
        }

        // TODO: Confirm me
        if (npc.isInCombat()) {
            _statusMask |= 0x01;
        }
        if (npc.isDead()) {
            _statusMask |= 0x02;
        }
        if (npc.isTargetable()) {
            _statusMask |= 0x04;
        }
        if (npc.isShowName()) {
            _statusMask |= 0x08;
        }

        if (_statusMask != 0) {
            addComponentType(NpcInfoType.VISUAL_STATE);
        }
    }

    @Override
    protected byte[] getMasks() {
        return _masks;
    }

    @Override
    protected void onNewMaskAdded(NpcInfoType component) {
        calcBlockSize(_npc, component);
    }

    private void calcBlockSize(Npc npc, NpcInfoType type) {
        switch (type) {
            case ATTACKABLE:
            case RELATIONS: {
                _initSize += type.getBlockLength();
                break;
            }
            case TITLE: {
                _initSize += type.getBlockLength() + (npc.getTitle().length() * 2);
                break;
            }
            case NAME: {
                _blockSize += type.getBlockLength() + (npc.getName().length() * 2);
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
        writeId(ServerPacketId.NPC_INFO, buffer );

        buffer.writeInt(_npc.getObjectId());
        buffer.writeByte((_npc.isShowSummonAnimation() ? 0x02 : 0x00)); // // 0=teleported 1=default 2=summoned
        buffer.writeShort(37); // mask_bits_37
        buffer.writeBytes(_masks);

        // Block 1
        buffer.writeByte(_initSize);

        if (containsMask(NpcInfoType.ATTACKABLE)) {
            buffer.writeByte(isAttackable(_npc) && !(_npc instanceof Guard));
        }
        if (containsMask(NpcInfoType.RELATIONS)) {
            buffer.writeInt(0x00); // unknown
        }
        if (containsMask(NpcInfoType.TITLE)) {
            buffer.writeString(_npc.getTitle());
        }

        // Block 2
        buffer.writeShort( _blockSize);
        if (containsMask(NpcInfoType.ID)) {
            buffer.writeInt(_npc.getTemplate().getDisplayId() + 1000000);
        }
        if (containsMask(NpcInfoType.POSITION)) {
            buffer.writeInt(_npc.getX());
            buffer.writeInt(_npc.getY());
            buffer.writeInt(_npc.getZ());
        }
        if (containsMask(NpcInfoType.HEADING)) {
            buffer.writeInt(_npc.getHeading());
        }
        if (containsMask(NpcInfoType.UNKNOWN2)) {
            buffer.writeInt(0x00); // Unknown
        }
        if (containsMask(NpcInfoType.ATK_CAST_SPEED)) {
            buffer.writeInt(_npc.getPAtkSpd());
            buffer.writeInt(_npc.getMAtkSpd());
        }
        if (containsMask(NpcInfoType.SPEED_MULTIPLIER)) {
            buffer.writeFloat((float) _npc.getStats().getMovementSpeedMultiplier());
            buffer.writeFloat((float) _npc.getStats().getAttackSpeedMultiplier());
        }
        if (containsMask(NpcInfoType.EQUIPPED)) {
            buffer.writeInt(_npc.getRightHandItem());
            buffer.writeInt(0x00); // Armor id?
            buffer.writeInt(_npc.getLeftHandItem());
        }
        if (containsMask(NpcInfoType.ALIVE)) {
            buffer.writeByte(!_npc.isDead());
        }
        if (containsMask(NpcInfoType.RUNNING)) {
            buffer.writeByte(_npc.isRunning());
        }
        if (containsMask(NpcInfoType.SWIM_OR_FLY)) {
            buffer.writeByte((_npc.isInsideZone(ZoneType.WATER) ? 0x01 : _npc.isFlying() ? 0x02 : 0x00));
        }
        if (containsMask(NpcInfoType.TEAM)) {
            buffer.writeByte(_npc.getTeam().getId());
        }
        if (containsMask(NpcInfoType.ENCHANT)) {
            buffer.writeInt(_npc.getEnchantEffect());
        }
        if (containsMask(NpcInfoType.FLYING)) {
            buffer.writeInt(_npc.isFlying());
        }
        if (containsMask(NpcInfoType.CLONE)) {
            buffer.writeInt(_npc.getCloneObjId()); // Player ObjectId with Decoy
        }
        if (containsMask(NpcInfoType.COLOR_EFFECT)) {
            buffer.writeInt(_npc.getColorEffect()); // Color effect
        }
        if (containsMask(NpcInfoType.DISPLAY_EFFECT)) {
            buffer.writeInt(_npc.getDisplayEffect());
        }
        if (containsMask(NpcInfoType.TRANSFORMATION)) {
            buffer.writeInt(_npc.getTransformationDisplayId()); // Transformation ID
        }
        if (containsMask(NpcInfoType.CURRENT_HP)) {
            buffer.writeInt((int) _npc.getCurrentHp());
        }
        if (containsMask(NpcInfoType.CURRENT_MP)) {
            buffer.writeInt((int) _npc.getCurrentMp());
        }
        if (containsMask(NpcInfoType.MAX_HP)) {
            buffer.writeInt(_npc.getMaxHp());
        }
        if (containsMask(NpcInfoType.MAX_MP)) {
            buffer.writeInt(_npc.getMaxMp());
        }
        if (containsMask(NpcInfoType.SUMMONED)) {
            buffer.writeByte(0x00); // 2 - do some animation on spawn
        }
        if (containsMask(NpcInfoType.UNKNOWN12)) {
            buffer.writeInt(0x00);
            buffer.writeInt(0x00);
        }
        if (containsMask(NpcInfoType.NAME)) {
            buffer.writeString(_npc.getName());
        }
        if (containsMask(NpcInfoType.NAME_NPCSTRINGID)) {
            final NpcStringId nameString = _npc.getNameString();
            buffer.writeInt(nameString != null ? nameString.getId() : -1); // NPCStringId for name
        }
        if (containsMask(NpcInfoType.TITLE_NPCSTRINGID)) {
            final NpcStringId titleString = _npc.getTitleString();
            buffer.writeInt(titleString != null ? titleString.getId() : -1); // NPCStringId for title
        }
        if (containsMask(NpcInfoType.PVP_FLAG)) {
            buffer.writeByte(_npc.getPvpFlag()); // PVP flag
        }
        if (containsMask(NpcInfoType.REPUTATION)) {
            buffer.writeInt(_npc.getReputation()); // Reputation
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
            buffer.writeShort((_abnormalVisualEffects.size() + (_npc.isInvisible() ? 1 : 0)));
            for (AbnormalVisualEffect abnormalVisualEffect : _abnormalVisualEffects) {
                buffer.writeShort(abnormalVisualEffect.getClientId());
            }
            if (_npc.isInvisible()) {
                buffer.writeShort(AbnormalVisualEffect.STEALTH.getClientId());
            }
        }
    }

}