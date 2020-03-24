package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Column;
import org.l2j.commons.database.annotation.Table;

/**
 * @author malyelfik
 * @author JoeAlisson
 */
@Table("castle_manor_procure")
public final class CropProcure extends SeedProduction {

    @Column("reward_type")
    private int rewardType;

    public CropProcure(int id, long amount, int type, long startAmount, long price, int castleId, boolean nextPeriod) {
        super(id, amount, price, startAmount, castleId, nextPeriod);
        rewardType = type;
    }

    public final int getReward() {
        return rewardType;
    }
}