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

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

/**
 * @author JoeAlisson
 */
@Table("clan_data")
public class ClanData {

    @Column("clan_id")
    private int id;

    @Column("clan_name")
    private String name;

    @Column("clan_level")
    private int level;

    @Column("reputation_score")
    private int reputation;

    @Column("hasCastle")
    private int castle;

    @Column("blood_alliance_count")
    private int bloodAllianceCount;

    @Column("ally_id")
    private int allyId;

    @Column("ally_name")
    private String allyName;

    @Column("leader_id")
    private int leaderId;

    @Column("crest_id")
    private int crest;

    @Column("crest_large_id")
    private int crestLarge;

    @Column("ally_crest_id")
    private int allyCrest;

    @Column("ally_penalty_expiry_time")
    private long allyPenaltyExpiryTime;

    @Column("ally_penalty_type")
    private int allyPenaltyType;

    @Column("char_penalty_expiry_time")
    private long charPenaltyExpiryTime;

    @Column("dissolving_expiry_time")
    private long dissolvingExpiryTime;

    @Column("new_leader_id")
    private int newLeaderId;

    public int getId() {
        return id;
    }

    public void setId(int clanId) {
        this.id = clanId;
    }

    public String getName() {
        return name;
    }

    public void setName(String clanName) {
        this.name = clanName;
    }

    public int getAllyId() {
        return allyId;
    }

    public void setAllyId(int allyId) {
        this.allyId = allyId;
    }

    public String getAllyName() {
        return allyName;
    }

    public void setAllyName(String allyName) {
        this.allyName = allyName;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    public int getCastle() {
        return castle;
    }

    public void setCastle(int castleId) {
        this.castle = castleId;
    }

    public int getBloodAllianceCount() {
        return bloodAllianceCount;
    }

    public void setBloodAllianceCount(int count) {
        bloodAllianceCount = count;
    }

    public int getLeaderId() {
        return leaderId;
    }

    public void setLeader(int leaderId) {
        this.leaderId = leaderId;
    }

    public int getCrest() {
        return crest;
    }

    public void setCrest(int crestId) {
        this.crest = crestId;
    }

    public int getCrestLarge() {
        return crestLarge;
    }

    public void setCrestLarge(int crestLargeId) {
        this.crestLarge = crestLargeId;
    }

    public int getAllyCrest() {
        return allyCrest;
    }

    public void setAllyCrest(int allyCrestId) {
        this.allyCrest = allyCrestId;
    }

    public long getAllyPenaltyExpiryTime() {
        return allyPenaltyExpiryTime;
    }

    public void setAllyPenaltyExpiryTime(long expiryTime) {
        allyPenaltyExpiryTime = expiryTime;
    }

    public int getAllyPenaltyType() {
        return allyPenaltyType;
    }

    public void setAllyPenaltyType(int penaltyType) {
        this.allyPenaltyType = penaltyType;
    }

    public long getCharPenaltyExpiryTime() {
        return charPenaltyExpiryTime;
    }

    public void setCharPenaltyExpiryTime(long time) {
        this.charPenaltyExpiryTime = time;
    }

    public long getDissolvingExpiryTime() {
        return dissolvingExpiryTime;
    }

    public void setDissolvingExpiryTime(long time) {
        this.dissolvingExpiryTime = time;
    }

    public int getNewLeaderId() {
        return newLeaderId;
    }

    public void setNewLeader(int leaderId) {
        this.leaderId = leaderId;
    }

    public String toString() {
        return name + " [" + id + "]";
    }
}
