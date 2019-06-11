package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.ai.CtrlIntention;
import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.FortSiegeManager;
import org.l2j.gameserver.instancemanager.SiegeGuardManager;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PetInstance;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;

import java.nio.ByteBuffer;

public final class RequestPetGetItem extends IClientIncomingPacket {
    private int _objectId;

    @Override
    public void readImpl() {
        _objectId = readInt();
    }

    @Override
    public void runImpl() {
        final L2World world = L2World.getInstance();
        final L2ItemInstance item = (L2ItemInstance) world.findObject(_objectId);
        if ((item == null) || (client.getActiveChar() == null) || !client.getActiveChar().hasPet()) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final Castle castle = CastleManager.getInstance().getCastle(item);
        if ((castle != null) && (SiegeGuardManager.getInstance().getSiegeGuardByItem(castle.getResidenceId(), item.getId()) != null)) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (FortSiegeManager.getInstance().isCombat(item.getId())) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        final L2PetInstance pet = client.getActiveChar().getPet();
        if (pet.isDead() || pet.isControlBlocked()) {
            client.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        if (pet.isUncontrollable()) {
            client.sendPacket(SystemMessageId.WHEN_YOUR_PET_S_HUNGER_GAUGE_IS_AT_0_YOU_CANNOT_USE_YOUR_PET);
            return;
        }

        pet.getAI().setIntention(CtrlIntention.AI_INTENTION_PICK_UP, item);
    }

}
