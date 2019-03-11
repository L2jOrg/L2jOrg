package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.enums.NpcInfoType;
import org.l2j.gameserver.enums.Team;
import org.l2j.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.skills.AbnormalVisualEffect;
import org.l2j.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Set;

/**
 * @author Sdw
 */
public class ExPetInfo extends AbstractMaskPacket<NpcInfoType> {
    private final L2Summon _summon;
    private final L2PcInstance _attacker;
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

    public ExPetInfo(L2Summon summon, L2PcInstance attacker, int val) {
        _summon = summon;
        _attacker = attacker;
        _title = (summon.getOwner() != null) && summon.getOwner().isOnline() ? summon.getOwner().getName() : "";
        _val = val;
        _abnormalVisualEffects = summon.getEffectList().getCurrentAbnormalVisualEffects();

        if (summon.getTemplate().getDisplayId() != summon.getTemplate().getId()) {
            _masks[2] |= 0x10;
            addComponentType(NpcInfoType.NAME);
        }

        addComponentType(NpcInfoType.ATTACKABLE, NpcInfoType.UNKNOWN1, NpcInfoType.TITLE, NpcInfoType.ID, NpcInfoType.POSITION, NpcInfoType.ALIVE, NpcInfoType.RUNNING, NpcInfoType.PVP_FLAG);

        if (summon.getHeading() > 0) {
            addComponentType(NpcInfoType.HEADING);
        }

        if ((summon.getStat().getPAtkSpd() > 0) || (summon.getStat().getMAtkSpd() > 0)) {
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

        if (summon.isInsideZone(ZoneId.WATER) || summon.isFlying()) {
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
        calcBlockSize(_summon, component);
    }

    private void calcBlockSize(L2Summon summon, NpcInfoType type) {
        switch (type) {
            case ATTACKABLE:
            case UNKNOWN1: {
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
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.EX_PET_INFO.writeId(packet);

        packet.putInt(_summon.getObjectId());
        packet.put((byte) _val); // // 0=teleported 1=default 2=summoned
        packet.putShort((short) 37); // mask_bits_37
        packet.put(_masks);

        // Block 1
        packet.put((byte) _initSize);

        if (containsMask(NpcInfoType.ATTACKABLE)) {
            packet.put((byte) (_summon.isAutoAttackable(_attacker) ? 0x01 : 0x00));
        }
        if (containsMask(NpcInfoType.UNKNOWN1)) {
            packet.putInt(0x00); // unknown
        }
        if (containsMask(NpcInfoType.TITLE)) {
            writeString(_title, packet);
        }

        // Block 2
        packet.putShort((short) _blockSize);
        if (containsMask(NpcInfoType.ID)) {
            packet.putInt(_summon.getTemplate().getDisplayId() + 1000000);
        }
        if (containsMask(NpcInfoType.POSITION)) {
            packet.putInt(_summon.getX());
            packet.putInt(_summon.getY());
            packet.putInt(_summon.getZ());
        }
        if (containsMask(NpcInfoType.HEADING)) {
            packet.putInt(_summon.getHeading());
        }
        if (containsMask(NpcInfoType.UNKNOWN2)) {
            packet.putInt(0x00); // Unknown
        }
        if (containsMask(NpcInfoType.ATK_CAST_SPEED)) {
            packet.putInt(_summon.getPAtkSpd());
            packet.putInt(_summon.getMAtkSpd());
        }
        if (containsMask(NpcInfoType.SPEED_MULTIPLIER)) {
            packet.putFloat((float) _summon.getStat().getMovementSpeedMultiplier());
            packet.putFloat((float) _summon.getStat().getAttackSpeedMultiplier());
        }
        if (containsMask(NpcInfoType.EQUIPPED)) {
            packet.putInt(_summon.getWeapon());
            packet.putInt(_summon.getArmor()); // Armor id?
            packet.putInt(0x00);
        }
        if (containsMask(NpcInfoType.ALIVE)) {
            packet.put((byte) (_summon.isDead() ? 0x00 : 0x01));
        }
        if (containsMask(NpcInfoType.RUNNING)) {
            packet.put((byte)(_summon.isRunning() ? 0x01 : 0x00));
        }
        if (containsMask(NpcInfoType.SWIM_OR_FLY)) {
            packet.put((byte) (_summon.isInsideZone(ZoneId.WATER) ? 0x01 : _summon.isFlying() ? 0x02 : 0x00));
        }
        if (containsMask(NpcInfoType.TEAM)) {
            packet.put((byte) _summon.getTeam().getId());
        }
        if (containsMask(NpcInfoType.ENCHANT)) {
            packet.putInt(_summon.getTemplate().getWeaponEnchant());
        }
        if (containsMask(NpcInfoType.FLYING)) {
            packet.putInt(_summon.isFlying() ? 0x01 : 00);
        }
        if (containsMask(NpcInfoType.CLONE)) {
            packet.putInt(0x00); // Player ObjectId with Decoy
        }
        if (containsMask(NpcInfoType.COLOR_EFFECT)) {
            // No visual effect
            packet.putInt(0x00); // Unknown
        }
        if (containsMask(NpcInfoType.DISPLAY_EFFECT)) {
            packet.putInt(0x00);
        }
        if (containsMask(NpcInfoType.TRANSFORMATION)) {
            packet.putInt(_summon.getTransformationDisplayId()); // Transformation ID
        }
        if (containsMask(NpcInfoType.CURRENT_HP)) {
            packet.putInt((int) _summon.getCurrentHp());
        }
        if (containsMask(NpcInfoType.CURRENT_MP)) {
            packet.putInt((int) _summon.getCurrentMp());
        }
        if (containsMask(NpcInfoType.MAX_HP)) {
            packet.putInt(_summon.getMaxHp());
        }
        if (containsMask(NpcInfoType.MAX_MP)) {
            packet.putInt(_summon.getMaxMp());
        }
        if (containsMask(NpcInfoType.SUMMONED)) {
            packet.put((byte) (_summon.isShowSummonAnimation() ? 0x02 : 0x00)); // 2 - do some animation on spawn
        }
        if (containsMask(NpcInfoType.UNKNOWN12)) {
            packet.putInt(0x00);
            packet.putInt(0x00);
        }
        if (containsMask(NpcInfoType.NAME)) {
            writeString(_summon.getName(), packet);
        }
        if (containsMask(NpcInfoType.NAME_NPCSTRINGID)) {
            packet.putInt(-1); // NPCStringId for name
        }
        if (containsMask(NpcInfoType.TITLE_NPCSTRINGID)) {
            packet.putInt(-1); // NPCStringId for title
        }
        if (containsMask(NpcInfoType.PVP_FLAG)) {
            packet.put((byte) _summon.getPvpFlag()); // PVP flag
        }
        if (containsMask(NpcInfoType.REPUTATION)) {
            packet.putInt(_summon.getReputation()); // Name color
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
            packet.putShort((short) _abnormalVisualEffects.size());
            for (AbnormalVisualEffect abnormalVisualEffect : _abnormalVisualEffects) {
                packet.putShort((short) abnormalVisualEffect.getClientId());
            }
        }
    }
}