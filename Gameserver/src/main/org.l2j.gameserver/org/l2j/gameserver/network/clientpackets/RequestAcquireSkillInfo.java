package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.model.ClanPrivilege;
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.base.AcquireSkillType;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.serverpackets.AcquireSkillInfo;
import org.l2j.gameserver.network.serverpackets.ExAcquireSkillInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Request Acquire Skill Info client packet implementation.
 *
 * @author Zoey76
 */
public final class RequestAcquireSkillInfo extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestAcquireSkillInfo.class);
    private int _id;
    private int _level;
    private AcquireSkillType _skillType;

    @Override
    public void readImpl() {
        _id = readInt();
        _level = readInt();
        _skillType = AcquireSkillType.getAcquireSkillType(readInt());
    }

    @Override
    public void runImpl() {
        if ((_id <= 0) || (_level <= 0)) {
            LOGGER.warn(RequestAcquireSkillInfo.class.getSimpleName() + ": Invalid Id: " + _id + " or level: " + _level + "!");
            return;
        }

        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final Npc trainer = activeChar.getLastFolkNPC();
        if ((_skillType != AcquireSkillType.CLASS) && ((trainer == null) || !trainer.isNpc() || (!trainer.canInteract(activeChar) && !activeChar.isGM()))) {
            return;
        }

        final Skill skill = SkillData.getInstance().getSkill(_id, _level);
        if (skill == null) {
            LOGGER.warn("Skill Id: " + _id + " level: " + _level + " is undefined. " + RequestAcquireSkillInfo.class.getName() + " failed.");
            return;
        }

        // Hack check. Doesn't apply to all Skill Types
        final int prevSkillLevel = activeChar.getSkillLevel(_id);
        if ((prevSkillLevel > 0) && !((_skillType == AcquireSkillType.SUBPLEDGE))) {
            if (prevSkillLevel == _level) {
                LOGGER.warn(RequestAcquireSkillInfo.class.getSimpleName() + ": Player " + activeChar.getName() + " is requesting info for a skill that already knows, Id: " + _id + " level: " + _level + "!");
            } else if (prevSkillLevel != (_level - 1)) {
                LOGGER.warn(RequestAcquireSkillInfo.class.getSimpleName() + ": Player " + activeChar.getName() + " is requesting info for skill Id: " + _id + " level " + _level + " without knowing it's previous level!");
            }
        }

        final SkillLearn s = SkillTreesData.getInstance().getSkillLearn(_skillType, _id, _level, activeChar);
        if (s == null) {
            return;
        }

        switch (_skillType) {
            case TRANSFORM:
            case FISHING: {
                client.sendPacket(new AcquireSkillInfo(_skillType, s));
                break;
            }
            case CLASS: {
                client.sendPacket(new ExAcquireSkillInfo(activeChar, s));
                break;
            }
            case PLEDGE: {
                if (!activeChar.isClanLeader()) {
                    return;
                }
                client.sendPacket(new AcquireSkillInfo(_skillType, s));
                break;
            }
            case SUBPLEDGE: {
                if (!activeChar.isClanLeader() || !activeChar.hasClanPrivilege(ClanPrivilege.CL_TROOPS_FAME)) {
                    return;
                }
                client.sendPacket(new AcquireSkillInfo(_skillType, s));
                break;
            }
        }
    }
}
