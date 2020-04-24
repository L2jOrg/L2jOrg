package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.model.WorldObject;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.serverpackets.ExRpItemLink;
import org.l2j.gameserver.world.World;

import static org.l2j.gameserver.util.GameUtils.isItem;

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
        final WorldObject object = World.getInstance().findObject(_objectId);
        if (isItem(object)) {
            final Item item = (Item) object;
            if (item.isPublished()) {
                client.sendPacket(new ExRpItemLink(item));
            }
        }
    }
}
