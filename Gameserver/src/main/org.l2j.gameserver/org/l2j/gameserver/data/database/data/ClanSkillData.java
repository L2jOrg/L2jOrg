package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

/**
 * @author JoeAlisson
 */
@Table("clan_skills")
public class ClanSkillData {

    @Column("clan_id")
    private int clanId;

    @Column("skill_id")
    private int id;

    @Column("skill_level")
    private int level;

    @Column("sub_pledge_id")
    private int subPledge;

    public int getClanId() {
        return clanId;
    }

    public int getId() {
        return id;
    }

    public int getLevel() {
        return level;
    }

    public int getSubPledge() {
        return subPledge;
    }
}
