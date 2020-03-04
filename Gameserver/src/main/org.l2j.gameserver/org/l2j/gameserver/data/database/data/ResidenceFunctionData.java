package org.l2j.gameserver.data.database.data;

import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.data.sql.impl.ClanTable;
import org.l2j.gameserver.data.xml.impl.ResidenceFunctionsData;
import org.l2j.gameserver.model.Clan;
import org.l2j.gameserver.model.itemcontainer.ItemContainer;
import org.l2j.gameserver.model.items.instance.Item;
import org.l2j.gameserver.model.residences.AbstractResidence;
import org.l2j.gameserver.model.residences.ResidenceFunctionTemplate;
import org.l2j.gameserver.model.residences.ResidenceFunctionType;
import org.l2j.gameserver.network.serverpackets.AgitDecoInfo;

import java.time.Instant;
import java.util.concurrent.ScheduledFuture;

import static java.util.Objects.isNull;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public class ResidenceFunctionData {
    private int id;
    private int level;
    private long expiration;

    private AbstractResidence residence;
    private ScheduledFuture<?> _task;

    public ResidenceFunctionData() {
    }

    public ResidenceFunctionData(int id, int level, long expiration, AbstractResidence residence) {
        this.id = id;
        this.level = level;
        this.expiration = expiration;
        this.residence = residence;
        init();
    }

    public ResidenceFunctionData(int id, int level, AbstractResidence residence) {
        this.id = id;
        this.level = level;
        final ResidenceFunctionTemplate template = getTemplate();
        expiration = Instant.now().toEpochMilli() + template.getDuration().toMillis();
        this.residence = residence;
        init();
    }

    /**
     * Initializes the function task
     */
    private void init() {
        final ResidenceFunctionTemplate template = getTemplate();
        if ((template != null) && (expiration > System.currentTimeMillis())) {
            _task = ThreadPool.schedule(this::onFunctionExpiration, expiration - System.currentTimeMillis());
        }
    }

    /**
     * @return the function id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the function level
     */
    public int getLevel() {
        return level;
    }

    /**
     * @return the expiration of this function instance
     */
    public long getExpiration() {
        return expiration;
    }

    /**
     * @return the owner (clan) of this function instance
     */
    public int getOwnerId() {
        return residence.getOwnerId();
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
        return ResidenceFunctionsData.getInstance().getFunction(id, level);
    }

    /**
     * The function invoked when task run, it either re-activate the function or removes it (In case clan doesn't cannot pay for it)
     */
    private void onFunctionExpiration() {
        if (!reactivate()) {
            residence.removeFunction(this);

            final Clan clan = ClanTable.getInstance().getClan(residence.getOwnerId());
            if (clan != null) {
                clan.broadcastToOnlineMembers(new AgitDecoInfo(residence));
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

        final Clan clan = ClanTable.getInstance().getClan(residence.getOwnerId());
        if (clan == null) {
            return false;
        }

        final ItemContainer wh = clan.getWarehouse();
        final Item item = wh.getItemByItemId(template.getCost().getId());
        if ((item == null) || (item.getCount() < template.getCost().getCount())) {
            return false;
        }

        if (wh.destroyItem("FunctionFee", item, template.getCost().getCount(), null, this) != null) {
            expiration = System.currentTimeMillis() + (template.getDuration().getSeconds() * 1000);
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

    public void initResidence(AbstractResidence residence) {
        this.residence = residence;
        if(isNull(_task)) {
            init();
        }
    }
}
