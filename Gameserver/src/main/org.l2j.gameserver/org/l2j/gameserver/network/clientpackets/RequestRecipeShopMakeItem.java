package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.RecipeController;
import org.l2j.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.util.Util;

import java.nio.ByteBuffer;

/**
 * @author Administrator
 */
public final class RequestRecipeShopMakeItem extends IClientIncomingPacket {
    private int _id;
    private int _recipeId;
    @SuppressWarnings("unused")
    private long _unknown;

    @Override
    public void readImpl(ByteBuffer packet) {
        _id = packet.getInt();
        _recipeId = packet.getInt();
        _unknown = packet.getLong();
    }

    @Override
    public void runImpl() {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        if (!client.getFloodProtectors().getManufacture().tryPerformAction("RecipeShopMake")) {
            return;
        }

        final L2PcInstance manufacturer = L2World.getInstance().getPlayer(_id);
        if (manufacturer == null) {
            return;
        }

        if (manufacturer.getInstanceWorld() != activeChar.getInstanceWorld()) {
            return;
        }

        if (activeChar.getPrivateStoreType() != PrivateStoreType.NONE) {
            activeChar.sendMessage("You cannot create items while trading.");
            return;
        }
        if (manufacturer.getPrivateStoreType() != PrivateStoreType.MANUFACTURE) {
            // activeChar.sendMessage("You cannot create items while trading.");
            return;
        }

        if (activeChar.isCrafting() || manufacturer.isCrafting()) {
            activeChar.sendMessage("You are currently in Craft Mode.");
            return;
        }
        if (Util.checkIfInRange(150, activeChar, manufacturer, true)) {
            RecipeController.getInstance().requestManufactureItem(manufacturer, _recipeId, activeChar);
        }
    }
}
