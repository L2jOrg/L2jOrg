/*
 * Copyright © 2019 L2J Mobius
 * Copyright © 2019-2021 L2JOrg
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

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.NonUpdatable;
import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.engine.clan.ClanEngine;
import org.l2j.gameserver.model.Clan;

/**
 * @author Sdw
 * @author JoeAlisson
 */
@Table("pledge_recruit")
public class PledgeRecruitData {

    @Column("clan_id")
    private int clanId;

    private int karma;
    private String information;

    @Column("detailed_information")
    private String detailedInformation;

    @Column("application_type")
    private int applicationType;

    @Column("recruit_type")
    private int recruitType;

    @NonUpdatable
    private Clan clan;

    public PledgeRecruitData() {

    }

    public PledgeRecruitData(int clanId, int karma, String information, String detailedInformation, int applicationType, int recruitType) {
        this.clanId = clanId;
        this.karma = karma;
        this.information = information;
        this.detailedInformation = detailedInformation;
        clan = ClanEngine.getInstance().getClan(clanId);
        this.applicationType = applicationType;
        this.recruitType = recruitType;
    }

    public int getClanId() {
        return clanId;
    }

    public String getClanName() {
        return clan.getName();
    }

    public String getClanLeaderName() {
        return clan.getLeaderName();
    }

    public int getClanLevel() {
        return clan.getLevel();
    }

    public int getKarma() {
        return karma;
    }

    public String getInformation() {
        return information;
    }

    public String getDetailedInformation() {
        return detailedInformation;
    }

    public int getApplicationType() {
        return applicationType;
    }

    public int getRecruitType() {
        return recruitType;
    }

    public Clan getClan() {
        return clan;
    }

    public void setClan(Clan clan) {
        this.clan = clan;
    }
}
