package org.l2j.gameserver.model.buylist;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.commons.threading.ThreadPool;
import org.l2j.gameserver.Config;
import org.l2j.gameserver.model.items.BodyPart;
import org.l2j.gameserver.model.items.EquipableItem;
import org.l2j.gameserver.model.items.ItemTemplate;
import org.l2j.gameserver.model.items.type.CrystalType;
import org.l2j.gameserver.model.items.type.EtcItemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.Objects;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author NosBit
 */
public final class Product {
    private static final Logger LOGGER = LoggerFactory.getLogger(Product.class);

    private final int _buyListId;
    private final ItemTemplate template;
    private final long _price;
    private final long _restockDelay;
    private final long _maxCount;
    private final double _baseTax;
    private AtomicLong _count = null;
    private ScheduledFuture<?> _restockTask = null;

    public Product(int buyListId, ItemTemplate item, long price, long restockDelay, long maxCount, int baseTax) {
        Objects.requireNonNull(item);
        _buyListId = buyListId;
        template = item;
        _price = (price < 0) ? item.getReferencePrice() : price;
        _restockDelay = restockDelay * 60000;
        _maxCount = maxCount;
        _baseTax = baseTax / 100.0;
        if (hasLimitedStock()) {
            _count = new AtomicLong(maxCount);
        }
    }

    public ItemTemplate getTemplate() {
        return template;
    }

    public int getItemId() {
        return template.getId();
    }

    public long getPrice() {
        long price = _price;
        if (template.getItemType().equals(EtcItemType.CASTLE_GUARD)) {
            price *= Config.RATE_SIEGE_GUARDS_PRICE;
        }
        return price;
    }

    public double getBaseTaxRate() {
        return _baseTax;
    }

    public long getRestockDelay() {
        return _restockDelay;
    }

    public long getMaxCount() {
        return _maxCount;
    }

    public long getCount() {
        if (_count == null) {
            return 0;
        }
        final long count = _count.get();
        return count > 0 ? count : 0;
    }

    public void setCount(long currentCount) {
        if (_count == null) {
            _count = new AtomicLong();
        }
        _count.set(currentCount);
    }

    public boolean decreaseCount(long val) {
        if (_count == null) {
            return false;
        }
        if ((_restockTask == null) || _restockTask.isDone()) {
            _restockTask = ThreadPool.schedule(this::restock, _restockDelay);
        }
        final boolean result = _count.addAndGet(-val) >= 0;
        save();
        return result;
    }

    public boolean hasLimitedStock() {
        return _maxCount > -1;
    }

    public void restartRestockTask(long nextRestockTime) {
        final long remainTime = nextRestockTime - System.currentTimeMillis();
        if (remainTime > 0) {
            _restockTask = ThreadPool.schedule(this::restock, remainTime);
        } else {
            restock();
        }
    }

    public void restock() {
        setCount(_maxCount);
        save();
    }

    private void save() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement("INSERT INTO `buylists`(`buylist_id`, `item_id`, `count`, `next_restock_time`) VALUES(?, ?, ?, ?) ON DUPLICATE KEY UPDATE `count` = ?, `next_restock_time` = ?")) {
            statement.setInt(1, _buyListId);
            statement.setInt(2, template.getId());
            statement.setLong(3, getCount());
            statement.setLong(5, getCount());
            if ((_restockTask != null) && (_restockTask.getDelay(TimeUnit.MILLISECONDS) > 0)) {
                final long nextRestockTime = System.currentTimeMillis() + _restockTask.getDelay(TimeUnit.MILLISECONDS);
                statement.setLong(4, nextRestockTime);
                statement.setLong(6, nextRestockTime);
            } else {
                statement.setLong(4, 0);
                statement.setLong(6, 0);
            }
            statement.executeUpdate();
        } catch (Exception e) {
            LOGGER.warn("Failed to save Product buylist_id:" + _buyListId + " item_id:" + template.getId(), e);
        }
    }

    public boolean isStackable() {
        return template.isStackable();
    }

    public long getWeight() {
        return template.getWeight();
    }

    public CrystalType getCrystalType() {
        return template.getCrystalType();
    }

    public boolean isEquipable() {
        return template instanceof EquipableItem;
    }

    public int getType2() {
        return template.getType2();
    }

    public int getType1() {
        return template.getType1();
    }

    public BodyPart getBodyPart() {
        return template.getBodyPart();
    }
}
