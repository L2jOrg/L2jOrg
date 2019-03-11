package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.model.skills.Skill;

import java.nio.ByteBuffer;

/**
 * @author KenM
 */
public class RequestDispel extends IClientIncomingPacket {
    private int _objectId;
    private int _skillId;
    private int _skillLevel;
    private int _skillSubLevel;

    @Override
    public void readImpl(ByteBuffer packet) {
        _objectId = packet.getInt();
        _skillId = packet.getInt();
        _skillLevel = packet.getShort();
        _skillSubLevel = packet.getShort();
    }

    @Override
    public void runImpl() {
        if ((_skillId <= 0) || (_skillLevel <= 0)) {
            return;
        }
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }
        final Skill skill = SkillData.getInstance().getSkill(_skillId, _skillLevel, _skillSubLevel);
        if (skill == null) {
            return;
        }
        if (!skill.canBeDispelled() || skill.isStayAfterDeath() || skill.isDebuff()) {
            return;
        }
        if (skill.getAbnormalType() == AbnormalType.TRANSFORM) {
            return;
        }
        if (skill.isDance() && !Config.DANCE_CANCEL_BUFF) {
            return;
        }
        if (activeChar.getObjectId() == _objectId) {
            activeChar.stopSkillEffects(true, _skillId);
        } else {
            final L2Summon pet = activeChar.getPet();
            if ((pet != null) && (pet.getObjectId() == _objectId)) {
                pet.stopSkillEffects(true, _skillId);
            }

            final L2Summon servitor = activeChar.getServitor(_objectId);
            if (servitor != null) {
                servitor.stopSkillEffects(true, _skillId);
            }
        }
    }
}
