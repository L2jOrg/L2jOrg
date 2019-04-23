package org.l2j.gameserver.model.skills;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Mobius
 */
public class MountEnabledSkillList
{
    private final static List<Integer> ENABLED_SKILLS = new ArrayList<>(2);
    {
        ENABLED_SKILLS.add(4289); // Wyvern Breath
        ENABLED_SKILLS.add(325); // Strider Siege Assault
    }

    public static boolean contains(int skillId)
    {
        return ENABLED_SKILLS.contains(skillId);
    }
}
