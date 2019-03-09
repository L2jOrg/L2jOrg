package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.RecipeController;
import org.l2j.gameserver.mobius.gameserver.enums.PrivateStoreType;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;

import java.nio.ByteBuffer;

/**
 * @author Administrator
 */
public final class RequestRecipeItemMakeSelf extends IClientIncomingPacket
{
    private int _id;

    @Override
    public void readImpl(ByteBuffer packet)
    {
        _id = packet.getInt();
    }

    @Override
    public void runImpl()
    {
        final L2PcInstance activeChar = client.getActiveChar();
        if (activeChar == null)
        {
            return;
        }

        if (!client.getFloodProtectors().getManufacture().tryPerformAction("RecipeMakeSelf"))
        {
            return;
        }

        if (activeChar.getPrivateStoreType() != PrivateStoreType.NONE)
        {
            activeChar.sendMessage("You cannot create items while trading.");
            return;
        }

        if (activeChar.isCrafting())
        {
            activeChar.sendMessage("You are currently in Craft Mode.");
            return;
        }

        RecipeController.getInstance().requestMakeItem(activeChar, _id);
    }
}
