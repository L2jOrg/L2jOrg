package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.L2SkillLearn;
import org.l2j.gameserver.model.base.AcquireSkillType;
import org.l2j.gameserver.model.holders.ItemHolder;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.ServerPacketId;

import java.util.ArrayList;
import java.util.List;

/**
 * Acquire Skill Info server packet implementation.
 *
 * @author Zoey76
 */
public class AcquireSkillInfo extends ServerPacket {
    private final AcquireSkillType _type;
    private final int _id;
    private final int _level;
    private final long _spCost;
    private final List<Req> _reqs;

    /**
     * Constructor for the acquire skill info object.
     *
     * @param skillType  the skill learning type.
     * @param skillLearn the skill learn.
     */
    public AcquireSkillInfo(AcquireSkillType skillType, L2SkillLearn skillLearn) {
        _id = skillLearn.getSkillId();
        _level = skillLearn.getSkillLevel();
        _spCost = skillLearn.getLevelUpSp();
        _type = skillType;
        _reqs = new ArrayList<>();
        if ((skillType != AcquireSkillType.PLEDGE) || Config.LIFE_CRYSTAL_NEEDED) {
            for (ItemHolder item : skillLearn.getRequiredItems()) {
                if (!Config.DIVINE_SP_BOOK_NEEDED && (_id == CommonSkill.DIVINE_INSPIRATION.getId())) {
                    continue;
                }
                _reqs.add(new Req(99, item.getId(), item.getCount(), 50));
            }
        }
    }

    /**
     * Special constructor for Alternate Skill Learning system.<br>
     * Sets a custom amount of SP.
     *
     * @param skillType  the skill learning type.
     * @param skillLearn the skill learn.
     * @param sp         the custom SP amount.
     */
    public AcquireSkillInfo(AcquireSkillType skillType, L2SkillLearn skillLearn, int sp) {
        _id = skillLearn.getSkillId();
        _level = skillLearn.getSkillLevel();
        _spCost = sp;
        _type = skillType;
        _reqs = new ArrayList<>();
        for (ItemHolder item : skillLearn.getRequiredItems()) {
            _reqs.add(new Req(99, item.getId(), item.getCount(), 50));
        }
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(ServerPacketId.ACQUIRE_SKILL_INFO);

        writeInt(_id);
        writeInt(_level);
        writeLong(_spCost);
        writeInt(_type.getId());
        writeInt(_reqs.size());
        for (Req temp : _reqs) {
            writeInt(temp.type);
            writeInt(temp.itemId);
            writeLong(temp.count);
            writeInt(temp.unk);
        }
    }


    /**
     * Private class containing learning skill requisites.
     */
    private static class Req {
        public int itemId;
        public long count;
        public int type;
        public int unk;

        /**
         * @param pType     TODO identify.
         * @param pItemId   the item Id.
         * @param itemCount the item count.
         * @param pUnk      TODO identify.
         */
        public Req(int pType, int pItemId, long itemCount, int pUnk) {
            itemId = pItemId;
            type = pType;
            count = itemCount;
            unk = pUnk;
        }
    }
}
