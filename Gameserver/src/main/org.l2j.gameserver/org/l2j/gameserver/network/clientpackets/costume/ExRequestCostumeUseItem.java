package org.l2j.gameserver.network.clientpackets.costume;

import org.l2j.gameserver.engine.costume.CostumeEngine;
import org.l2j.gameserver.enums.ItemSkillType;
import org.l2j.gameserver.model.skills.SkillCaster;
import org.l2j.gameserver.network.clientpackets.ClientPacket;

import static java.util.Objects.nonNull;

/**
 * @author JoeAlisson
 */
public class ExRequestCostumeUseItem extends ClientPacket {

    private int itemObjectId;

    @Override
    protected void readImpl() throws Exception {
        itemObjectId = readInt();
    }

    @Override
    protected void runImpl() {
        var player = client.getPlayer();
        var item = player.getInventory().getItemByObjectId(itemObjectId);
        if(nonNull(item) && CostumeEngine.getInstance().checkCostumeAction(player)) {
            item.forEachSkill(ItemSkillType.NORMAL, skill -> SkillCaster.triggerCast(player, player, skill.getSkill(), item, true));
        }
    }
}
