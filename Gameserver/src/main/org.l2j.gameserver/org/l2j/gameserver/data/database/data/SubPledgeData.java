package org.l2j.gameserver.data.database.data;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;
import org.l2j.commons.database.annotation.Transient;
import org.l2j.gameserver.engine.skill.api.Skill;

import java.util.Collection;

/**
 * @author JoeAlisson
 */
@Table("clan_subpledges")
public class SubPledgeData {

    @Transient
    private final IntMap<Skill> skills = new CHashIntMap<>();

    @Column("clan_id")
    private int clanId;

    @Column("sub_pledge_id")
    private int id;

    private String name;

    @Column("leader_id")
    private int leaderId;

    public int getClanId() {
        return clanId;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getLeaderId() {
        return leaderId;
    }

    public void setClanId(int clanId) {
        this.clanId = clanId;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLeaderId(int leaderId) {
        this.leaderId = leaderId;
    }

    public Skill addNewSkill(Skill skill) {
        return skills.put(skill.getId(), skill);
    }

    public Skill getSkill(int skillId) {
        return skills.get(skillId);
    }

    public Collection<Skill> getSkills() {
        return skills.values();
    }
}
