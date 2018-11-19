package l2s.gameserver.utils;

import l2s.gameserver.Config;
import l2s.gameserver.data.xml.holder.SkillAcquireHolder;
import l2s.gameserver.data.xml.holder.SkillHolder;
import l2s.gameserver.model.Player;
import l2s.gameserver.model.SkillLearn;
import l2s.gameserver.model.base.AcquireType;
import l2s.gameserver.skills.SkillEntry;

/**
 * @author Bonux
 **/
public final class SkillUtils
{
    public static int generateSkillHashCode(int id, int level)
    {
        return id * 1000 + level;
    }

    public static int getSkillIdFromPTSHash(int hash)
    {
        final int mask = 0b1111111111111111;
        return mask & hash >>> 16;
    }

    public static int getSkillLevelFromPTSHash(int hash)
    {
        final int mask = 0b1111111111111111;
        return mask & hash;
    }

    public static int getSkillIdFromPTSLongHash(long hash)
    {
        final int mask = 0b1111111111111111;
        return (int) (mask & hash >>> 32);
    }

    public static int getSkillLevelFromPTSLongHash(long hash)
    {
        final int mask = 0b1111111111111111;
        return (int) (mask & hash);
    }

    public static boolean checkSkill(Player player, SkillEntry skillEntry)
    {
        if(!Config.ALT_REMOVE_SKILLS_ON_DELEVEL)
            return false;

        SkillLearn learn = SkillAcquireHolder.getInstance().getSkillLearn(player, skillEntry.getId(), skillEntry.getLevel(), AcquireType.NORMAL);
        if(learn == null)
            return false;

        boolean update = false;

        int lvlDiff = learn.isFreeAutoGet(AcquireType.NORMAL) ? 1 : 4;
        if(learn.getMinLevel() >= (player.getLevel() + lvlDiff))
        {
            player.removeSkill(skillEntry, true);

            // если у нас низкий лвл для скила, то заточка обнуляется 100%
            // и ищем от большего к меньшему подходящий лвл для скила
            for(int i = skillEntry.getLevel() - 1; i != 0; i--)
            {
                SkillLearn learn2 = SkillAcquireHolder.getInstance().getSkillLearn(player, skillEntry.getId(), i, AcquireType.NORMAL);
                if(learn2 == null)
                    continue;

                int lvlDiff2 = learn2.isFreeAutoGet(AcquireType.NORMAL) ? 1 : 4;
                if(learn2.getMinLevel() >= (player.getLevel() + lvlDiff2))
                    continue;

                SkillEntry newSkillEntry = SkillHolder.getInstance().getSkillEntry(skillEntry.getId(), i);
                if(newSkillEntry != null)
                {
                    player.addSkill(newSkillEntry, true);
                    break;
                }
            }
            update = true;
        }

        if(player.isTransformed())
        {
            learn = player.getTransform().getAdditionalSkill(skillEntry.getId(), skillEntry.getLevel());
            if(learn == null)
                return false;

            if(learn.getMinLevel() >= player.getLevel() + 1)
            {
                player.removeTransformSkill(skillEntry);
                player.removeSkill(skillEntry, false);

                for(int i = skillEntry.getLevel() - 1; i != 0; i--)
                {
                    SkillLearn learn2 = player.getTransform().getAdditionalSkill(skillEntry.getId(), i);
                    if(learn2 == null)
                        continue;

                    if(learn2.getMinLevel() >= player.getLevel() + 1)
                        continue;

                    SkillEntry newSkillEntry = SkillHolder.getInstance().getSkillEntry(skillEntry.getId(), i);
                    if(newSkillEntry != null)
                    {
                        player.addTransformSkill(newSkillEntry);
                        player.addSkill(newSkillEntry, false);
                        break;
                    }
                }
                update = true;
            }
        }
        return update;
    }
}