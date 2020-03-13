package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Table;

/**
 * @author JoeAlisson
 */
@Table("character_summons")
public class SummonData {
    private int ownerId;
    private int summonId;
    private int summonSkillId;
    private int curHp;
    private int curMp;
    private int time;

    public int getOwnerId() {
        return ownerId;
    }

    public int getSummonId() {
        return summonId;
    }

    public int getSummonSkillId() {
        return summonSkillId;
    }

    public int getCurHp() {
        return curHp;
    }

    public int getCurMp() {
        return curMp;
    }

    public int getTime() {
        return time;
    }
}
