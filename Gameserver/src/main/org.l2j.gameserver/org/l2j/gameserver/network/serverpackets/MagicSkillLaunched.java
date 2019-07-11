package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.skills.SkillCastingType;
import org.l2j.gameserver.network.GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;

/**
 * MagicSkillLaunched server packet implementation.
 *
 * @author UnAfraid
 */
public class MagicSkillLaunched extends ServerPacket {
    private final int _charObjId;
    private final int _skillId;
    private final int _skillLevel;
    private final SkillCastingType _castingType;
    private final Collection<WorldObject> _targets;

    public MagicSkillLaunched(Creature cha, int skillId, int skillLevel, SkillCastingType castingType, Collection<WorldObject> targets) {
        _charObjId = cha.getObjectId();
        _skillId = skillId;
        _skillLevel = skillLevel;
        _castingType = castingType;

        if (targets == null) {
            targets = Collections.singletonList(cha);
        }

        _targets = targets;
    }

    public MagicSkillLaunched(Creature cha, int skillId, int skillLevel, SkillCastingType castingType, WorldObject... targets) {
        this(cha, skillId, skillLevel, castingType, (targets == null ? Collections.singletonList(cha) : Arrays.asList(targets)));
    }

    public MagicSkillLaunched(Creature cha, int skillId, int skillLevel) {
        this(cha, skillId, skillId, SkillCastingType.NORMAL, Collections.singletonList(cha));
    }

    @Override
    public void writeImpl(GameClient client) {
        writeId(ServerPacketId.MAGIC_SKILL_LAUNCHED);

        writeInt(_castingType.getClientBarId()); // MagicSkillUse castingType
        writeInt(_charObjId);
        writeInt(_skillId);
        writeInt(_skillLevel);
        writeInt(_targets.size());
        for (WorldObject target : _targets) {
            writeInt(target.getObjectId());
        }
    }

}
