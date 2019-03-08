package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.L2Object;
import org.l2j.gameserver.mobius.gameserver.model.actor.L2Character;
import org.l2j.gameserver.mobius.gameserver.model.skills.SkillCastingType;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * MagicSkillLaunched server packet implementation.
 * @author UnAfraid
 */
public class MagicSkillLaunched implements IClientOutgoingPacket
{
    private final int _charObjId;
    private final int _skillId;
    private final int _skillLevel;
    private final SkillCastingType _castingType;
    private final Collection<L2Object> _targets;

    public MagicSkillLaunched(L2Character cha, int skillId, int skillLevel, SkillCastingType castingType, Collection<L2Object> targets)
    {
        _charObjId = cha.getObjectId();
        _skillId = skillId;
        _skillLevel = skillLevel;
        _castingType = castingType;

        if (targets == null)
        {
            targets = Collections.singletonList(cha);
        }

        _targets = targets;
    }

    public MagicSkillLaunched(L2Character cha, int skillId, int skillLevel, SkillCastingType castingType, L2Object... targets)
    {
        this(cha, skillId, skillLevel, castingType, (targets == null ? Collections.singletonList(cha) : Arrays.asList(targets)));
    }

    public MagicSkillLaunched(L2Character cha, int skillId, int skillLevel)
    {
        this(cha, skillId, skillId, SkillCastingType.NORMAL, Collections.singletonList(cha));
    }

    @Override
    public boolean write(PacketWriter packet)
    {
        OutgoingPackets.MAGIC_SKILL_LAUNCHED.writeId(packet);

        packet.writeD(_castingType.getClientBarId()); // MagicSkillUse castingType
        packet.writeD(_charObjId);
        packet.writeD(_skillId);
        packet.writeD(_skillLevel);
        packet.writeD(_targets.size());
        for (L2Object target : _targets)
        {
            packet.writeD(target.getObjectId());
        }
        return true;
    }
}
