package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.Config;
import org.l2j.gameserver.mobius.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.mobius.gameserver.enums.NpcInfoType;
import org.l2j.gameserver.mobius.gameserver.enums.Team;
import org.l2j.gameserver.mobius.gameserver.model.L2Clan;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Npc;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2GuardInstance;
import org.l2j.gameserver.mobius.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.mobius.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.NpcStringId;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Set;

/**
 * @author UnAfraid
 */
public class NpcInfo extends AbstractMaskPacket<NpcInfoType> {
    private final L2Npc _npc;
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

    public NpcInfo(L2Npc npc) {
        _npc = npc;
        _abnormalVisualEffects = npc.getEffectList().getCurrentAbnormalVisualEffects();

        addComponentType(NpcInfoType.ATTACKABLE, NpcInfoType.UNKNOWN1, NpcInfoType.ID, NpcInfoType.POSITION, NpcInfoType.ALIVE, NpcInfoType.RUNNING);

        if (npc.getHeading() > 0) {
            addComponentType(NpcInfoType.HEADING);
        }

        if ((npc.getStat().getPAtkSpd() > 0) || (npc.getStat().getMAtkSpd() > 0)) {
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

        if (npc.isInsideZone(ZoneId.WATER) || npc.isFlying()) {
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

        if (npc.getTemplate().isUsingServerSideTitle() || (Config.SHOW_NPC_LVL && npc.isMonster()) || npc.isChampion() || npc.isTrap()) {
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
            final L2Clan clan = ClanTable.getInstance().getClan(npc.getClanId());
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

    private void calcBlockSize(L2Npc npc, NpcInfoType type) {
        switch (type) {
            case ATTACKABLE:
            case UNKNOWN1: {
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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.NPC_INFO.writeId(packet);

        packet.putInt(_npc.getObjectId());
        packet.put((byte) (_npc.isShowSummonAnimation() ? 0x02 : 0x00)); // // 0=teleported 1=default 2=summoned
        packet.putShort((short) 37); // mask_bits_37
        packet.put(_masks);

        // Block 1
        packet.put((byte) _initSize);

        if (containsMask(NpcInfoType.ATTACKABLE)) {
            packet.put((byte)(_npc.isAttackable() && !(_npc instanceof L2GuardInstance) ? 0x01 : 0x00));
        }
        if (containsMask(NpcInfoType.UNKNOWN1)) {
            packet.putInt(0x00); // unknown
        }
        if (containsMask(NpcInfoType.TITLE)) {
            writeString(_npc.getTitle(), packet);
        }

        // Block 2
        packet.putShort((short) _blockSize);
        if (containsMask(NpcInfoType.ID)) {
            packet.putInt(_npc.getTemplate().getDisplayId() + 1000000);
        }
        if (containsMask(NpcInfoType.POSITION)) {
            packet.putInt(_npc.getX());
            packet.putInt(_npc.getY());
            packet.putInt(_npc.getZ());
        }
        if (containsMask(NpcInfoType.HEADING)) {
            packet.putInt(_npc.getHeading());
        }
        if (containsMask(NpcInfoType.UNKNOWN2)) {
            packet.putInt(0x00); // Unknown
        }
        if (containsMask(NpcInfoType.ATK_CAST_SPEED)) {
            packet.putInt(_npc.getPAtkSpd());
            packet.putInt(_npc.getMAtkSpd());
        }
        if (containsMask(NpcInfoType.SPEED_MULTIPLIER)) {
            packet.putFloat((float) _npc.getStat().getMovementSpeedMultiplier());
            packet.putFloat((float) _npc.getStat().getAttackSpeedMultiplier());
        }
        if (containsMask(NpcInfoType.EQUIPPED)) {
            packet.putInt(_npc.getRightHandItem());
            packet.putInt(0x00); // Armor id?
            packet.putInt(_npc.getLeftHandItem());
        }
        if (containsMask(NpcInfoType.ALIVE)) {
            packet.put((byte) (_npc.isDead() ? 0x00 : 0x01));
        }
        if (containsMask(NpcInfoType.RUNNING)) {
            packet.put((byte) (_npc.isRunning() ? 0x01 : 0x00));
        }
        if (containsMask(NpcInfoType.SWIM_OR_FLY)) {
            packet.put((byte) (_npc.isInsideZone(ZoneId.WATER) ? 0x01 : _npc.isFlying() ? 0x02 : 0x00));
        }
        if (containsMask(NpcInfoType.TEAM)) {
            packet.put((byte) _npc.getTeam().getId());
        }
        if (containsMask(NpcInfoType.ENCHANT)) {
            packet.putInt(_npc.getEnchantEffect());
        }
        if (containsMask(NpcInfoType.FLYING)) {
            packet.putInt(_npc.isFlying() ? 0x01 : 00);
        }
        if (containsMask(NpcInfoType.CLONE)) {
            packet.putInt(_npc.getCloneObjId()); // Player ObjectId with Decoy
        }
        if (containsMask(NpcInfoType.COLOR_EFFECT)) {
            packet.putInt(_npc.getColorEffect()); // Color effect
        }
        if (containsMask(NpcInfoType.DISPLAY_EFFECT)) {
            packet.putInt(_npc.getDisplayEffect());
        }
        if (containsMask(NpcInfoType.TRANSFORMATION)) {
            packet.putInt(_npc.getTransformationDisplayId()); // Transformation ID
        }
        if (containsMask(NpcInfoType.CURRENT_HP)) {
            packet.putInt((int) _npc.getCurrentHp());
        }
        if (containsMask(NpcInfoType.CURRENT_MP)) {
            packet.putInt((int) _npc.getCurrentMp());
        }
        if (containsMask(NpcInfoType.MAX_HP)) {
            packet.putInt(_npc.getMaxHp());
        }
        if (containsMask(NpcInfoType.MAX_MP)) {
            packet.putInt(_npc.getMaxMp());
        }
        if (containsMask(NpcInfoType.SUMMONED)) {
            packet.put((byte) 0x00); // 2 - do some animation on spawn
        }
        if (containsMask(NpcInfoType.UNKNOWN12)) {
            packet.putInt(0x00);
            packet.putInt(0x00);
        }
        if (containsMask(NpcInfoType.NAME)) {
            writeString(_npc.getName(), packet);
        }
        if (containsMask(NpcInfoType.NAME_NPCSTRINGID)) {
            final NpcStringId nameString = _npc.getNameString();
            packet.putInt(nameString != null ? nameString.getId() : -1); // NPCStringId for name
        }
        if (containsMask(NpcInfoType.TITLE_NPCSTRINGID)) {
            final NpcStringId titleString = _npc.getTitleString();
            packet.putInt(titleString != null ? titleString.getId() : -1); // NPCStringId for title
        }
        if (containsMask(NpcInfoType.PVP_FLAG)) {
            packet.put((byte) _npc.getPvpFlag()); // PVP flag
        }
        if (containsMask(NpcInfoType.REPUTATION)) {
            packet.putInt(_npc.getReputation()); // Reputation
        }
        if (containsMask(NpcInfoType.CLAN)) {
            packet.putInt(_clanId);
            packet.putInt(_clanCrest);
            packet.putInt(_clanLargeCrest);
            packet.putInt(_allyId);
            packet.putInt(_allyCrest);
        }

        if (containsMask(NpcInfoType.VISUAL_STATE)) {
            packet.put((byte) _statusMask);
        }

        if (containsMask(NpcInfoType.ABNORMALS)) {
            packet.putShort((short) (_abnormalVisualEffects.size() + (_npc.isInvisible() ? 1 : 0)));
            for (AbnormalVisualEffect abnormalVisualEffect : _abnormalVisualEffects) {
                packet.putShort((short) abnormalVisualEffect.getClientId());
            }
            if (_npc.isInvisible()) {
                packet.putShort((short) AbnormalVisualEffect.STEALTH.getClientId());
            }
        }
    }
}