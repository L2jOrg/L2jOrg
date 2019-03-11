package org.l2j.gameserver.handler;

import org.l2j.gameserver.model.items.L2EtcItem;

import java.util.HashMap;
import java.util.Map;

/**
 * This class manages handlers of items
 *
 * @author UnAfraid
 */
public class ItemHandler implements IHandler<IItemHandler, L2EtcItem> {
    private final Map<String, IItemHandler> _datatable;

    /**
     * Constructor of ItemHandler
     */
    protected ItemHandler() {
        _datatable = new HashMap<>();
    }

    /**
     * Create ItemHandler if doesn't exist and returns ItemHandler
     *
     * @return ItemHandler
     */
    public static ItemHandler getInstance() {
        return SingletonHolder._instance;
    }

    /**
     * Adds handler of item type in <I>datatable</I>.<BR>
     * <BR>
     * <B><I>Concept :</I></U><BR>
     * This handler is put in <I>datatable</I> Map &lt;String ; IItemHandler &gt; for each ID corresponding to an item type (existing in classes of package itemhandlers) sets as key of the Map.
     *
     * @param handler (IItemHandler)
     */
    @Override
    public void registerHandler(IItemHandler handler) {
        _datatable.put(handler.getClass().getSimpleName(), handler);
    }

    @Override
    public synchronized void removeHandler(IItemHandler handler) {
        _datatable.remove(handler.getClass().getSimpleName());
    }

    /**
     * Returns the handler of the item
     *
     * @param item
     * @return IItemHandler
     */
    @Override
    public IItemHandler getHandler(L2EtcItem item) {
        if ((item == null) || (item.getHandlerName() == null)) {
            return null;
        }
        return _datatable.get(item.getHandlerName());
    }

    /**
     * Returns the number of elements contained in datatable
     *
     * @return int : Size of the datatable
     */
    @Override
    public int size() {
        return _datatable.size();
    }

    private static class SingletonHolder {
        protected static final ItemHandler _instance = new ItemHandler();
    }
}
