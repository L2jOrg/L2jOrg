/*
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
import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.enums.ShotType;

/**
 * @author JoeAlisson
 */
@Table("player_variables")
public class PlayerVariableData {

    public static final int REVENGE_USABLE_FUNCTIONS = 5;

    @Column("player_id")
    private int playerId;

    @Column("revenge_teleports")
    private byte revengeTeleports;

    @Column("revenge_locations")
    private byte revengeLocations;

    @Column("hair_accessory_enabled")
    private boolean hairAccessoryEnabled;

    @Column("world_chat_used")
    private int worldChatUsed;

    @Column("sayha_grace_items_used")
    private int sayhaGraceItemsUsed;

    @Column("fortune_telling")
    private int fortuneTelling;

    @Column("fortune_telling_black_cat")
    private boolean fortuneTellingBlackCat;

    private int autoCp;

    private int autoHp;

    private int autoMp;

    @Column("exp_off")
    private boolean expOff;

    @Column("items_rewarded")
    private boolean itemsRewarded;

    @Column("henna1_duration")
    private long henna1Duration;

    @Column("henna2_duration")
    private long henna2Duration;

    @Column("henna3_duration")
    private long henna3Duration;

    @Column("visual_hair_id")
    private int visualHairId;

    @Column("visual_hair_color_id")
    private int visualHairColorId;

    @Column("visual_face_id")
    private int visualFaceId;

    @Column("instance_restore")
    private int instanceRestore;

    @Column("claimed_clan_rewards")
    private int claimedClanRewards;

    @Column("cond_override_key")
    private String condOverrideKey;

    @Column("monster_return")
    private int monsterReturn;

    @Column("lamp_xp")
    private int lampXp;

    @Column("lamp_count")
    private int lampCount;

    @Column("saya_support")
    private long sayaSupportXp;

    @Column("saya_limited")
    private long sayaLimitedXp;

    private int soulshot;
    private int spiritshot;

    public boolean isHairAccessoryEnabled() {
        return hairAccessoryEnabled;
    }

    public int getWorldChatUsed() {
        return worldChatUsed;
    }

    public int getSayhaGraceItemsUsed() {
        return sayhaGraceItemsUsed;
    }

    public void setHairAccessoryEnabled(boolean hairAccessoryEnabled) {
        this.hairAccessoryEnabled = hairAccessoryEnabled;
    }

    public void setWorldChatUsed(int worldChatUsed) {
        this.worldChatUsed = worldChatUsed;
    }

    public String getExtendDrop() {
        return extendDrop;
    }

    public void setExtendDrop(String extendDrop) {
        this.extendDrop = extendDrop;
    }

    public int getFortuneTelling() {
        return fortuneTelling;
    }

    public void setFortuneTelling(int fortuneTelling) {
        this.fortuneTelling = fortuneTelling;
    }

    public void setFortuneTellingBlackCat(boolean fortuneTellingBlackCat) {
        this.fortuneTellingBlackCat = fortuneTellingBlackCat;
    }

    public boolean isFortuneTellingBlackCat() {
        return fortuneTellingBlackCat;
    }

    public void setAutoCp(int autoCp) {
        this.autoCp = autoCp;
    }

    public int getAutoCp() {
        return autoCp;
    }

    public void setAutoHp(int autoHp) {
        this.autoHp = autoHp;
    }

    public int getAutoHp() {
        return autoHp;
    }

    public void setAutoMp(int autoMp) {
        this.autoMp = autoMp;
    }

    public int getAutoMp() {
        return autoMp;
    }

    public void setExpOff(boolean expOff) {
        this.expOff = expOff;
    }

    public boolean getExpOff() {
        return expOff;
    }

    public void setItemsRewarded(boolean itemsRewarded) {
        this.itemsRewarded = itemsRewarded;
    }

    public boolean isItemsRewarded() {
        return itemsRewarded;
    }

    public void setHenna1Duration(long hennaDuration) {
        this.henna1Duration = hennaDuration;
    }

    public long getHenna1Duration() {
        return henna1Duration;
    }

    public void setHenna2Duration(long hennaDuration) {
        this.henna2Duration = hennaDuration;
    }

    public long getHenna2Duration() {
        return henna2Duration;
    }

    public void setHenna3Duration(long hennaDuration) {
        this.henna3Duration = hennaDuration;
    }

    public long getHenna3Duration() {
        return henna3Duration;
    }

    public void setVisualHairId(int visualHairId) {
        this.visualHairId = visualHairId;
    }

    public int getVisualHairId() {
        return visualHairId;
    }

    public void setVisualHairColorId(int visualHairColorId) {
        this.visualHairColorId = visualHairColorId;
    }

    public int getVisualHairColorId() {
        return visualHairColorId;
    }

    public void setVisualFaceId(int visualFaceId) {
        this.visualFaceId = visualFaceId;
    }

    public int getVisualFaceId() {
        return visualFaceId;
    }

    public void setInstanceRestore(int instanceRestore) {
        this.instanceRestore = instanceRestore;
    }

    public int getInstanceRestore() {
        return instanceRestore;
    }

    public void setClaimedClanRewards(int claimedClanRewards) {
        this.claimedClanRewards = claimedClanRewards;
    }

    public int getClaimedClanRewards() {
        return claimedClanRewards;
    }

    public void setCondOverrideKey(String condOverrideKey) {
        this.condOverrideKey = condOverrideKey;
    }

    public String getCondOverrideKey() {
        return condOverrideKey;
    }

    public void setMonsterReturn(int monsterReturn) {
        this.monsterReturn = monsterReturn;
    }

    public int getMonsterReturn() {
        return monsterReturn;
    }

    public byte getRevengeTeleports() {
        return revengeTeleports;
    }

    public byte getRevengeLocations() {
        return revengeLocations;
    }

    public void useRevengeLocation() {
        revengeLocations--;
    }

    public void useRevengeTeleport() {
        revengeTeleports--;
    }

    public void resetRevengeData() {
        revengeTeleports = REVENGE_USABLE_FUNCTIONS;
        revengeLocations = REVENGE_USABLE_FUNCTIONS;
    }

    public int getLampExp(){
        return lampXp;
    }

    public int getLampCount(){
        return lampCount;
    }

    public void setLampXp(int exp) {
        this.lampXp = exp;
    }

    public void setLampCount(int count) {
        this.lampCount = count;
    }

    public int getSoulshot() {
        return soulshot;
    }

    public int getSpiritshot() {
        return spiritshot;
    }

    public void updateActiveShot(ShotType type, int shotId) {
        if(type == ShotType.SOULSHOTS) {
            soulshot = shotId;
        } else if(type == ShotType.SPIRITSHOTS) {
            spiritshot = shotId;
        }
    }

    public static PlayerVariableData init(int playerId, byte face, byte hairStyle, byte hairColor) {
        var data = new PlayerVariableData();
        data.revengeTeleports = REVENGE_USABLE_FUNCTIONS;
        data.revengeLocations = REVENGE_USABLE_FUNCTIONS;
        data.playerId = playerId;
        data.visualFaceId = face;
        data.visualHairId = hairStyle;
        data.visualHairColorId = hairColor;
        return data;
    }
}
