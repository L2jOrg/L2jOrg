package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.data.xml.impl.SkillTreesData;
import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.SkillLearn;
import org.l2j.gameserver.model.actor.Npc;
import org.l2j.gameserver.model.actor.status.FolkStatus;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.model.base.AcquireSkillType;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ExAcquirableSkillListByClass;
import org.l2j.gameserver.network.serverpackets.SystemMessage;

import java.util.List;

public class Folk extends Npc {
    public Folk(NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2NpcInstance);
        setIsInvul(false);
    }

    /**
     * Displays Skill Tree for a given player, npc and class Id.
     *
     * @param player  the active character.
     * @param npc     the last folk.
     * @param classId player's active class id.
     */
    public static void showSkillList(Player player, Npc npc, ClassId classId) {
        // Normal skills, No LearnedByFS, no AutoGet skills.
        final List<SkillLearn> skills = SkillTreesData.getInstance().getAvailableSkills(player, classId, false, false);
        if (skills.isEmpty()) {
            final var skillTree = SkillTreesData.getInstance().getCompleteClassSkillTree(classId);
            final int minLevel = SkillTreesData.getInstance().getMinLevelForNewSkill(player, skillTree);
            if (minLevel > 0) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.YOU_DO_NOT_HAVE_ANY_FURTHER_SKILLS_TO_LEARN_COME_BACK_WHEN_YOU_HAVE_REACHED_LEVEL_S1);
                sm.addInt(minLevel);
                player.sendPacket(sm);
            } else if (player.getClassId().level() == 1) {
                final SystemMessage sm = SystemMessage.getSystemMessage(SystemMessageId.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN_PLEASE_COME_BACK_AFTER_S1ND_CLASS_CHANGE);
                sm.addInt(2);
                player.sendPacket(sm);
            } else {
                player.sendPacket(SystemMessageId.THERE_ARE_NO_OTHER_SKILLS_TO_LEARN);
            }
        } else {
            player.sendPacket(new ExAcquirableSkillListByClass(skills, AcquireSkillType.CLASS));
        }
    }

    @Override
    public FolkStatus getStatus() {
        return (FolkStatus) super.getStatus();
    }

    @Override
    public void initCharStatus() {
        setStatus(new FolkStatus(this));
    }
}
