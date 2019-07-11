package org.l2j.gameserver.model.residences;

import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.xml.impl.ResidenceFunctionsData;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.itemcontainer.ItemContainer;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.network.serverpackets.AgitDecoInfo;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

/**
 * @author UnAfraid
 */
public class ResidenceFunction {
    private final int _id;
    private final int _level;
    private final AbstractResidence _residense;
    private long _expiration;
    private ScheduledFuture<?> _task;

    public ResidenceFunction(int id, int level, long expiration, AbstractResidence residense) {
        _id = id;
        _level = level;
        _expiration = expiration;
        _residense = residense;
        init();
    }

    public ResidenceFunction(int id, int level, AbstractResidence residense) {
        _id = id;
        _level = level;
        final ResidenceFunctionTemplate template = getTemplate();
        _expiration = Instant.now().toEpochMilli() + template.getDuration().toMillis();
        _residense = residense;
        init();
    }

    /**
     * Initializes the function task
     */
    private void init() {
        final ResidenceFunctionTemplate template = getTemplate();
        if ((template != null) && (_expiration > System.currentTimeMillis())) {
            _task = ThreadPoolManager.getInstance().schedule(this::onFunctionExpiration, _expiration - System.currentTimeMillis());
        }
    }

    /**
     * @return the function id
     */
    public int getId() {
        return _id;
    }

    /**
     * @return the function level
     */
    public int getLevel() {
        return _level;
    }

    /**
     * @return the expiration of this function instance
     */
    public long getExpiration() {
        return _expiration;
    }

    /**
     * @return the owner (clan) of this function instance
     */
    public int getOwnerId() {
        return _residense.getOwnerId();
    }

    /**
     * @return value of the function
     */
    public double getValue() {
        final ResidenceFunctionTemplate template = getTemplate();
        return template == null ? 0 : template.getValue();
    }

    /**
     * @return the type of this function instance
     */
    public ResidenceFunctionType getType() {
        final ResidenceFunctionTemplate template = getTemplate();
        return template == null ? ResidenceFunctionType.NONE : template.getType();
    }

    /**
     * @return the template of this function instance
     */
    public ResidenceFunctionTemplate getTemplate() {
        return ResidenceFunctionsData.getInstance().getFunction(_id, _level);
    }

    /**
     * The function invoked when task run, it either re-activate the function or removes it (In case clan doesn't cannot pay for it)
     */
    private void onFunctionExpiration() {
        if (!reactivate()) {
            _residense.removeFunction(this);

            final L2Clan clan = ClanTable.getInstance().getClan(_residense.getOwnerId());
            if (clan != null) {
                clan.broadcastToOnlineMembers(new AgitDecoInfo(_residense));
            }
        }
    }

    /**
     * @return {@code true} if function instance is re-activated successfully, {@code false} otherwise
     */
    public boolean reactivate() {
        final ResidenceFunctionTemplate template = getTemplate();
        if (template == null) {
            return false;
        }

        final L2Clan clan = ClanTable.getInstance().getClan(_residense.getOwnerId());
        if (clan == null) {
            return false;
        }

        final ItemContainer wh = clan.getWarehouse();
        final Item item = wh.getItemByItemId(template.getCost().getId());
        if ((item == null) || (item.getCount() < template.getCost().getCount())) {
            return false;
        }

        if (wh.destroyItem("FunctionFee", item, template.getCost().getCount(), null, this) != null) {
            _expiration = System.currentTimeMillis() + (template.getDuration().getSeconds() * 1000);
            init();
        }
        return true;
    }

    /**
     * Cancels the task to {@link #onFunctionExpiration()}
     */
    public void cancelExpiration() {
        if ((_task != null) && !_task.isDone()) {
            _task.cancel(true);
        }
        _task = null;
    }
}
