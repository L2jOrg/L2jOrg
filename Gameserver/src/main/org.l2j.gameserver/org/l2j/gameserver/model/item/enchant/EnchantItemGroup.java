package org.l2j.gameserver.model.item.enchant;

import org.l2j.gameserver.model.holders.RangeChanceHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * @author UnAfraid
 */
public final class EnchantItemGroup {
    private static final Logger LOGGER = LoggerFactory.getLogger(EnchantItemGroup.class);
    private final List<RangeChanceHolder> _chances = new ArrayList<>();
    private final String _name;
    private byte _maximumEnchant = -1;

    public EnchantItemGroup(String name) {
        _name = name;
    }

    /**
     * @return name of current enchant item group.
     */
    public String getName() {
        return _name;
    }

    /**
     * @param holder
     */
    public void addChance(RangeChanceHolder holder) {
        _chances.add(holder);
    }

    /**
     * @param index
     * @return chance for success rate for current enchant item group.
     */
    public double getChance(int index) {
        if (!_chances.isEmpty()) {
            for (RangeChanceHolder holder : _chances) {
                if ((holder.getMin() <= index) && (holder.getMax() >= index)) {
                    return holder.getChance();
                }
            }
            LOGGER.warn(getClass().getSimpleName() + ": Couldn't match proper chance for item group: " + _name, new IllegalStateException());
            return _chances.get(_chances.size() - 1).getChance();
        }
        LOGGER.warn(": item group: " + _name + " doesn't have any chances!");
        return -1;
    }

    /**
     * @return the maximum enchant level for current enchant item group.
     */
    public byte getMaximumEnchant()
    {
        if (_maximumEnchant == -1)
        {
            for (RangeChanceHolder holder : _chances)
            {
                if ((holder.getChance() > 0) && (holder.getMax() > _maximumEnchant))
                {
                    _maximumEnchant = (byte) holder.getMax();
                }
            }
            _maximumEnchant++;
        }
        return _maximumEnchant;
    }
}
