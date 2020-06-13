/*
 * Copyright © 2019-2020 L2JOrg
 *
 * This file is part of the L2JOrg project.
 *
 * L2JOrg is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * L2JOrg is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.data.database.data;

import io.github.joealisson.primitive.CHashIntMap;
import io.github.joealisson.primitive.IntMap;
import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.engine.skill.api.Skill;

import java.util.Collection;

/**
 * @author JoeAlisson
 */
@Table("clan_subpledges")
public class SubPledgeData {

    @NonUpdatable
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
