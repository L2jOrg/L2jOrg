package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.L2Object;
import org.l2j.gameserver.model.Location;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.interfaces.IPositionable;
import org.l2j.gameserver.model.skills.SkillCastingType;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * MagicSkillUse server packet implementation.
 *
 * @author UnAfraid, NosBit
 */
public final class MagicSkillUse extends IClientOutgoingPacket {
    private final int _skillId;
    private final int _skillLevel;
    private final int _hitTime;
    private final int _reuseGroup;
    private final int _reuseDelay;
    private final int _actionId; // If skill is called from RequestActionUse, use that ID.
    private final SkillCastingType _castingType; // Defines which client bar is going to use.
    private final L2Character _activeChar;
    private final L2Object _target;
    private final List<Integer> _unknown = Collections.emptyList();
    private final List<Location> _groundLocations;

    public MagicSkillUse(L2Character cha, L2Object target, int skillId, int skillLevel, int hitTime, int reuseDelay, int reuseGroup, int actionId, SkillCastingType castingType) {
        _activeChar = cha;
        _target = target;
        _skillId = skillId;
        _skillLevel = skillLevel;
        _hitTime = hitTime;
        _reuseGroup = reuseGroup;
        _reuseDelay = reuseDelay;
        _actionId = actionId;
        _castingType = castingType;
        Location skillWorldPos = null;
        if (cha.isPlayer()) {
            final L2PcInstance player = cha.getActingPlayer();
            if (player.getCurrentSkillWorldPosition() != null) {
                skillWorldPos = player.getCurrentSkillWorldPosition();
            }
        }
        _groundLocations = skillWorldPos != null ? Arrays.asList(skillWorldPos) : Collections.emptyList();
    }

    public MagicSkillUse(L2Character cha, L2Object target, int skillId, int skillLevel, int hitTime, int reuseDelay) {
        this(cha, target, skillId, skillLevel, hitTime, reuseDelay, -1, -1, SkillCastingType.NORMAL);
    }

    public MagicSkillUse(L2Character cha, int skillId, int skillLevel, int hitTime, int reuseDelay) {
        this(cha, cha, skillId, skillLevel, hitTime, reuseDelay, -1, -1, SkillCastingType.NORMAL);
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.MAGIC_SKILL_USE.writeId(packet);

        packet.putInt(_castingType.getClientBarId()); // Casting bar type: 0 - default, 1 - default up, 2 - blue, 3 - green, 4 - red.
        packet.putInt(_activeChar.getObjectId());
        packet.putInt(_target.getObjectId());
        packet.putInt(_skillId);
        packet.putInt(_skillLevel);
        packet.putInt(_hitTime);
        packet.putInt(_reuseGroup);
        packet.putInt(_reuseDelay);
        packet.putInt(_activeChar.getX());
        packet.putInt(_activeChar.getY());
        packet.putInt(_activeChar.getZ());
        packet.putShort((short) _unknown.size()); // TODO: Implement me!
        for (int unknown : _unknown) {
            packet.putShort((short) unknown);
        }
        packet.putShort((short) _groundLocations.size());
        for (IPositionable target : _groundLocations) {
            packet.putInt(target.getX());
            packet.putInt(target.getY());
            packet.putInt(target.getZ());
        }
        packet.putInt(_target.getX());
        packet.putInt(_target.getY());
        packet.putInt(_target.getZ());
        packet.putInt(_actionId >= 0 ? 0x01 : 0x00); // 1 when ID from RequestActionUse is used
        packet.putInt(_actionId >= 0 ? _actionId : 0); // ID from RequestActionUse. Used to set cooldown on summon skills.
    }

    @Override
    protected int size(L2GameClient client) {
        return 74 + _unknown.size() * 2 + _groundLocations.size() * 12;
    }
}
