package org.l2j.gameserver.engine.upgrade;

import org.l2j.gameserver.model.holders.ItemHolder;

import java.util.List;

/**
 * @author JoeAlisson
 */
public interface Upgrade {

    List<ItemHolder> material();

    int item();

    int enchantment();

    long commission();
}
