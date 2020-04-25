package org.l2j.gameserver.model.actor.instance;

import org.l2j.gameserver.enums.InstanceType;
import org.l2j.gameserver.model.actor.Creature;
import org.l2j.gameserver.model.actor.templates.NpcTemplate;
import org.l2j.gameserver.util.GameUtils;

public class Warehouse extends Folk {
    public Warehouse(NpcTemplate template) {
        super(template);
        setInstanceType(InstanceType.L2WarehouseInstance);
    }

    @Override
    public boolean isAutoAttackable(Creature attacker) {
        if (GameUtils.isMonster(attacker)) {
            return true;
        }

        return super.isAutoAttackable(attacker);
    }

    @Override
    public boolean isWarehouse() {
        return true;
    }

    @Override
    public String getHtmlPath(int npcId, int val) {
        String pom = "";

        if (val == 0) {
            pom = Integer.toString(npcId);
        } else {
            pom = npcId + "-" + val;
        }

        return "data/html/warehouse/" + pom + ".htm";
    }
}
