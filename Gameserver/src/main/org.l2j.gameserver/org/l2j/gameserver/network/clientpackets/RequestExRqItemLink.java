package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.L2World;
import org.l2j.gameserver.model.items.instance.L2ItemInstance;
import org.l2j.gameserver.network.serverpackets.ExRpItemLink;

/**
 * @author KenM
 */
public class RequestExRqItemLink extends ClientPacket {
    private int _objectId;

    @Override
    public void readImpl() {
        _objectId = readInt();
    }

    @Override
    public void runImpl() {
        final WorldObject object = L2World.getInstance().findObject(_objectId);
        if ((object != null) && object.isItem()) {
            final L2ItemInstance item = (L2ItemInstance) object;
            if (item.isPublished()) {
                client.sendPacket(new ExRpItemLink(item));
            }
        }
    }
}
