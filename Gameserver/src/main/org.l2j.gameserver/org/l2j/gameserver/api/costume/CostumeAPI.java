package org.l2j.gameserver.api.costume;

import org.l2j.gameserver.engine.costume.Costume;
import org.l2j.gameserver.engine.costume.CostumeEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.costume.ExCostumeUseItem;
import org.l2j.gameserver.network.serverpackets.costume.ExSendCostumeList;

import java.util.EnumSet;

import static java.util.Objects.isNull;

/**
 * @author JoeAlisson
 */
public class CostumeAPI {

    public static void imprintCostumeOnPlayer(Player player, int costumeId) {
        imprintCostume(player, CostumeEngine.getInstance().getCostume(costumeId));
    }

    private static void imprintCostume(Player player, Costume costume) {
        if(isNull(costume)) {
            return;
        }

        var playerCostume = player.addCostume(costume.id());
        if(playerCostume.getAmount() == 1) {
            player.addSkill(costume.skill(), true);
            CostumeEngine.getInstance().checkCostumeCollection(player, costume.id());
        }

        player.sendPacket(new ExCostumeUseItem(costume.id(), true));
        player.sendPacket(new ExSendCostumeList(playerCostume));
    }

    public static void imprintRandomCostumeOnPlayer(Player player, EnumSet<CostumeGrade> grades) {
        imprintCostume(player, CostumeEngine.getInstance().getRandomCostume(grades));
    }
}
