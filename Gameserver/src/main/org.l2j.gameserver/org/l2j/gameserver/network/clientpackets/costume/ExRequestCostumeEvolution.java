package org.l2j.gameserver.network.clientpackets.costume;

import org.l2j.gameserver.data.database.data.CostumeData;
import org.l2j.gameserver.engine.costume.Costume;
import org.l2j.gameserver.engine.costume.CostumeEngine;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.clientpackets.ClientPacket;
import org.l2j.gameserver.network.serverpackets.costume.ExCostumeEvolution;
import org.l2j.gameserver.network.serverpackets.costume.ExSendCostumeList;

import java.util.HashSet;
import java.util.Set;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static org.l2j.gameserver.network.SystemMessageId.THIS_TRANSFORMATION_CANNOT_EVOLVE;
import static org.l2j.gameserver.network.SystemMessageId.YOU_DONT_HAVE_NECESSARY_ITEMS;

/**
 * @author JoeAlisson
 */
public class ExRequestCostumeEvolution extends ClientPacket {

    private int id;
    private CostumeInfo[] materials;
    private long materialAmount;
    private Set<CostumeData> modifiedCostumes;

    @Override
    protected void readImpl() throws Exception {
        readInt(); // amount costumes to evolve for now always 1
        id = readInt();
        readLong(); // amount of the costume for now always 1
        var costumesUsed = readInt();

        modifiedCostumes = new HashSet<>(costumesUsed + 2);
        materials = new CostumeInfo[costumesUsed];
        for (int i = 0; i < costumesUsed; i++) {
            var info =  new CostumeInfo(readInt(), readLong());
            materials[i] = info;
            materialAmount += info.amount;
        }
    }

    @Override
    protected void runImpl() {
        var player = client.getPlayer();
        var playerCostume = player.getCostume(id);

        if(isNull(playerCostume)) {
            client.sendPacket(THIS_TRANSFORMATION_CANNOT_EVOLVE);
            client.sendPacket(ExCostumeEvolution.failed());
            return;
        }

        var costume = CostumeEngine.getInstance().getCostume(id);
        if(canEvolve(player, costume) && consumeCostumesCost(player)) {
            playerCostume.reduceCount(1);
            modifiedCostumes.add(playerCostume);

            var resultCostume = player.addCostume(id + 1);
            client.sendPacket(ExCostumeEvolution.success(modifiedCostumes, resultCostume));

            modifiedCostumes.add(resultCostume);
            client.sendPacket(new ExSendCostumeList(modifiedCostumes));

            checkCostume(player, playerCostume);
            for (CostumeInfo material : materials) {
                checkCostume(player, player.getCostume(material.id));
            }
        } else {
            client.sendPacket(ExCostumeEvolution.failed());
        }
    }

    private void checkCostume(Player player, CostumeData costume) {
        if(nonNull(costume) && costume.getAmount() <= 0) {
            var costumeEngine = CostumeEngine.getInstance();
            player.removeCostume(costume.getId());
            player.removeSkill(costumeEngine.getCostumeSkill(costume.getId()));
            costumeEngine.checkCostumeCollection(player, costume.getId());
        }
    }

    private boolean canEvolve(Player player, Costume costume) {
        if(isNull(costume) || costume.evolutionFee() == 0) {
            player.sendPacket(THIS_TRANSFORMATION_CANNOT_EVOLVE);
            return false;
        } else if(materialAmount != costume.evolutionFee()) {
            player.sendPacket(YOU_DONT_HAVE_NECESSARY_ITEMS);
            return false;
        }
        return CostumeEngine.getInstance().checkCostumeAction(player);
    }

    private boolean consumeCostumesCost(Player player) {
        for (CostumeInfo material : materials) {
            var costume = player.getCostume(material.id);
            var amount = material.amount + (material.id == id ? 1 : 0);
            if(isNull(costume) || costume.getAmount() < amount) {
                player.sendPacket(YOU_DONT_HAVE_NECESSARY_ITEMS);
                return false;
            }
        }

        for (CostumeInfo material : materials) {
            var costume = player.getCostume(material.id);
            costume.reduceCount(material.amount);
            modifiedCostumes.add(costume);
        }
        return true;
    }

    private static class CostumeInfo {
        private final int id;
        private final long amount;

        private CostumeInfo(int id, long amount) {
            this.id = id;
            this.amount = amount;
        }
    }
}
