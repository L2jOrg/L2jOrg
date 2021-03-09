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

import java.time.LocalDateTime;

@Table("account_data")
public class AccountData {

    @Column("account")
    private String accountName;

    private int coin;

    @Column("vip_point")
    private long vipPoints;

    @Column("vip_tier_expiration")
    private long vipTierExpiration;

    @Column("sec_auth_password")
    private String secAuthPassword;

    @Column("sec_auth_attempts")
    private int secAuthAttempts;

    @Column("next_attendance")
    private LocalDateTime nextAttendance;

    @Column("last_attendance_reward")
    private byte lastAttendanceReward;

    @Column("vip_attendance_reward")
    private int vipAttendanceReward;

    public static AccountData of(String accountName) {
        var account = new AccountData();
        account.accountName =  accountName;
        account.nextAttendance = LocalDateTime.now();
        return account;
    }

    public String getAccountName() {
        return accountName;
    }

    public long getVipPoints() {
        return vipPoints;
    }

    public long getVipTierExpiration() {
        return vipTierExpiration;
    }

    public void updateVipPoints(long points) {
        this.vipPoints += points;
    }

    public int getCoin() {
        return coin;
    }

    public void updateCoins(int coins) {
        this.coin += coins;
    }

    public void setCoins(int coins) {
        this.coin = coins;
    }

    public void setVipTierExpiration(long expiration) {
        this.vipTierExpiration = expiration;
    }

    public String getSecAuthPassword() {
        return secAuthPassword;
    }

    public void setSecAuthPassword(String secAuthPassword) {
        this.secAuthPassword = secAuthPassword;
    }

    public int getSecAuthAttempts() {
        return secAuthAttempts;
    }

    public void setSecAuthAttempts(int secAuthAttempts) {
        this.secAuthAttempts = secAuthAttempts;
    }

    public int increaseSecAuthAttempts() {
        return ++secAuthAttempts;
    }

    public LocalDateTime nextAttendance() {
        return nextAttendance;
    }

    public void setNextAttendance(LocalDateTime nextAttendance) {
        this.nextAttendance = nextAttendance;
    }

    public byte lastAttendanceReward() {
        return lastAttendanceReward;
    }

    public void setLastAttendanceReward(byte reward) {
        lastAttendanceReward = reward;
    }

    public void updateAttendanceVipReward(int reward) {
        vipAttendanceReward |= 1 << reward;
    }

    public void setVipAttendanceReward(int vipAttendanceReward) {
        this.vipAttendanceReward = vipAttendanceReward;
    }

    public int vipAttendanceReward() {
        return vipAttendanceReward;
    }

    @Override
    public String toString() {
        return accountName;
    }
}
