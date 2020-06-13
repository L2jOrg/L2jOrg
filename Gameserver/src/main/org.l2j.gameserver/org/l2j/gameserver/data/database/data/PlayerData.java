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

import java.time.LocalDate;

public class PlayerData {

    private int charId;

    @Column("account_name")
    private String accountName;
    @Column("char_name")
    private String name;
    private byte level;

    @Column("sex")
    private boolean female;
    private byte face;
    private byte hairColor;
    private byte hairStyle;
    private int classId;

    private long lastAccess;
    private long exp;
    private long expBeforeDeath;
    private long sp;
    private boolean wantsPeace;
    private int heading;
    private int reputation;
    private int fame;
    private int raidBossPoints;

    @Column("pvpkills")
    private int pvp;

    @Column("pkkills")
    private int pk;
    private long onlineTime;
    private boolean nobless;

    @Column("clan_join_expiry_time")
    private long clanJoinExpiryTime;

    @Column("clan_create_expiry_time")
    private long clanCreateExpiryTime;

    @Column("pccafe_points")
    private int pcCafePoints;

    private int  clanId;

    @Column("power_grade")
    private int powerGrade;

    @Column("vitality_points")
    private int vitalityPoints;

    private int subPledge;
    private String title;
    private int accessLevel;

    @Column("title_color")
    private int titleColr;

    @Column("curHp")
    private double currentHp;

    @Column("curCp")
    private double currentCp;

    @Column("curMp")
    private double currentMp;

    @Column("base_class")
    private int baseClass;

    private int apprentice;
    private int sponsor;
    private int race;

    @Column("lvl_joined_academy")
    private int levelJoinedAcademy;
    private int x;
    private int y;
    private int z;
    private int bookMarkSlot;
    private LocalDate createDate;
    private String language;

    public int getCharId() {
        return charId;
    }

    public void setId(int charId) {
        this.charId = charId;
    }

    public boolean isFemale() {
        return female;
    }

    public void setFemale(boolean female) {
        this.female = female;
    }

    public byte getFace() {
        return face;
    }

    public void setFace(byte face) {
        this.face = face;
    }

    public byte getHairColor() {
        return hairColor;
    }

    public void setHairColor(byte hairColor) {
        this.hairColor = hairColor;
    }

    public byte getHairStyle() {
        return hairStyle;
    }

    public void setHairStyle(byte hairStyle) {
        this.hairStyle = hairStyle;
    }

    public int getClassId() {
        return classId;
    }

    public void setClassId(int classId) {
        this.classId = classId;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getLastAccess() {
        return lastAccess;
    }

    public long getExp() {
        return exp;
    }

    public long getExpBeforeDeath() {
        return expBeforeDeath;
    }

    public byte getLevel() {
        return level;
    }

    public long getSp() {
        return sp;
    }

    public boolean wantsPeace() {
        return wantsPeace;
    }

    public int getHeading() {
        return heading;
    }

    public int getReputation() {
        return reputation;
    }

    public int getFame() {
        return fame;
    }

    public int getRaidBossPoints() {
        return raidBossPoints;
    }

    public void setRaidbossPoints(int points) {
        raidBossPoints = points;
    }

    public int getPvP() {
        return pvp;
    }

    public int getPk() {
        return pk;
    }

    public long getOnlineTime() {
        return onlineTime;
    }

    public boolean isNobless() {
        return nobless;
    }

    public long getClanJoinExpiryTime() {
        return clanJoinExpiryTime;
    }

    public long getClanCreateExpiryTime() {
        return clanCreateExpiryTime;
    }

    public int getPcCafePoints() {
        return pcCafePoints;
    }

    public int getClanId() {
        return clanId;
    }

    public int getPowerGrade() {
        return powerGrade;
    }

    public int getVitalityPoints() {
        return vitalityPoints;
    }

    public int getSubPledge() {
        return subPledge;
    }

    public String getTitle() {
        return title;
    }

    public int getAccessLevel() {
        return accessLevel;
    }

    public int getTitleColor() {
        return titleColr;
    }

    public double getCurrentHp() {
        return currentHp;
    }

    public double getCurrentCp() {
        return currentCp;
    }

    public double getCurrentMp() {
        return currentMp;
    }

    public int getBaseClass() {
        return baseClass;
    }

    public void setBaseClass(int classId) {
        baseClass = classId;
    }

    public int getApprentice() {
        return apprentice;
    }

    public int getSponsor() {
        return sponsor;
    }

    public int getLevelJoinedAcademy() {
        return levelJoinedAcademy;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public int getBookMarkSlot() {
        return bookMarkSlot;
    }

    public LocalDate getCreateDate() {
        return createDate;
    }

    public void setCreateDate(LocalDate date) {
        createDate = date;
    }

    public String getLanguage() {
        return language;
    }

    public void setExpBeforeDeath(long exp) {
        expBeforeDeath = exp;
    }

    public void setClanJoinExpiryTime(long time) {
        clanJoinExpiryTime = time;
    }

    public void setClanCreateExpiryTime(long time) {
        clanCreateExpiryTime  = time;
    }

    public void setPcCafePoints(int pcCafePoints) {
        this.pcCafePoints = pcCafePoints;
    }

    public void setPowerGrade(int powerGrade) {
        this.powerGrade = powerGrade;
    }

    public void setSubPledge(int subPledge) {
        this.subPledge = subPledge;
    }

    public void setApprentice(int apprentice) {
        this.apprentice = apprentice;
    }

    public void setSponsor(int sponsor) {
        this.sponsor= sponsor;
    }

    public void setLevelJoinedAcademy(int level) {
        this.levelJoinedAcademy = level;
    }

    public void setObjectId(int objectId) {
        this.charId = objectId;
    }

    public int getRace() {
        return race;
    }
}

