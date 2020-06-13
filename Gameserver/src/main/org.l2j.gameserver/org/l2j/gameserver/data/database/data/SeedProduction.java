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
 * @author xban1x
 * @author JoeAlisson
 */
@Table("castle_manor_production")
public class SeedProduction {

    @Column("castle_id")
    private int castleId;
    @Column("seed_id")
    private int seedId;
    private long amount;
    @Column("start_amount")
    private long startAmount;
    private long price;
    @Column("next_period")
    private boolean nextPeriod;

    public SeedProduction() {
    }

    public SeedProduction(int id, long amount, long price, long startAmount, int castleId, boolean nextPeriod) {
        seedId = id;
        this.amount = amount;
        this.price = price;
        this.startAmount = startAmount;
        this.castleId = castleId;
        this.nextPeriod = nextPeriod;
    }

    public final int getSeedId() {
        return seedId;
    }

    public final long getAmount() {
        return amount;
    }

    public final void setAmount(long amount) {
        this.amount = amount;
    }

    public final long getPrice() {
        return price;
    }

    public final long getStartAmount() {
        return startAmount;
    }

    public boolean isNextPeriod() {
        return nextPeriod;
    }

    public synchronized boolean decreaseAmount(long val) {
        if(amount - val < 0) {
            return false;
        }
        amount -= val;
        return true;
    }
}