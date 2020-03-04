package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.FortSiegeManager;
import org.l2j.gameserver.instancemanager.SiegeGuardManager;
import org.l2j.gameserver.world.World;
import org.l2j.gameserver.model.actor.instance.Pet;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;

public final class RequestPetGetItem extends ClientPacket {
    private int _objectId;

    @Override
    public void readImpl() {
        _objectId = readInt();
    }

    @Override
    public void runImpl() {
        final World world = World.getInstance();
        final Item item = (Item) world.findObject(_objectId);
        if ((item == null) || (client.getPlayer() == null) || !client.getPlayer().hasPet()) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final Castle castle = CastleManager.getInstance().getCastle(item);
        if ((castle != null) && (SiegeGuardManager.getInstance().getSiegeGuardByItem(castle.getId(), item.getId()) != null)) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (FortSiegeManager.getInstance().isCombat(item.getId())) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final Pet pet = client.getPlayer().getPet();
        if (pet.isDead() || pet.isControlBlocked()) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (pet.isUncontrollable()) {
            client.sendPacket(SystemMessageId.YOU_CANNOT_USE_YOUR_PET_WHEN_ITS_HUNGER_GAUGE_IS_AT_0);
            return;
        }

        pet.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, item);
    }

}
