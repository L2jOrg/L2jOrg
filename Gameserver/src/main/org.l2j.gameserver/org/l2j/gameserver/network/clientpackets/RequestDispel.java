package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.engine.skill.api.SkillEngine;
import org.l2j.gameserver.model.actor.Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.AbnormalType;
import org.l2j.gameserver.engine.skill.api.Skill;

/**
 * @author KenM
 */
public class RequestDispel extends ClientPacket {
    private int _objectId;
    private int _skillId;
    private int _skillLevel;
    private int _skillSubLevel;

    @Override
    public void readImpl() {
        _objectId = readInt();
        _skillId = readInt();
        _skillLevel = readShort();
        _skillSubLevel = readShort();
    }

    @Override
    public void runImpl() {
        if ((_skillId <= 0) || (_skillLevel <= 0)) {
            return;
        }
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }
        final Skill skill = SkillEngine.getInstance().getSkill(_skillId, _skillLevel);
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
            final Summon pet = activeChar.getPet();
            if ((pet != null) && (pet.getObjectId() == _objectId)) {
                pet.stopSkillEffects(true, _skillId);
            }

            final Summon servitor = activeChar.getServitor(_objectId);
            if (servitor != null) {
                servitor.stopSkillEffects(true, _skillId);
            }
        }
    }
}
