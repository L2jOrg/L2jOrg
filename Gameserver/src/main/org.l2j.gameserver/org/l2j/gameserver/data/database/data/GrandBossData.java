package org.l2j.gameserver.data.database.data;

import org.l2j.commons.database.annotation.Table;
import org.l2j.gameserver.instancemanager.BossStatus;

/**
 * @author JoeAlisson
 */
@Table("grandboss_data")
public class GrandBossData extends BossData {

    private BossStatus status;

    public BossStatus getStatus() {
        return status;
    }

    public void setStatus(BossStatus status) {
        this.status = status;
    }
}
