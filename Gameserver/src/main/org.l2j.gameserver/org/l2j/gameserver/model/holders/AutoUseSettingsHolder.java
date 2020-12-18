package org.l2j.gameserver.model.holders;

import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class AutoUseSettingsHolder {

    private final Collection<Integer> _autoSkills = ConcurrentHashMap.newKeySet();
    private final Collection<Integer> _autoSupplyItems = ConcurrentHashMap.newKeySet();

    public AutoUseSettingsHolder()
    {
    }

    public Collection<Integer> getAutoSkills() {
        return _autoSkills;
    }

    public Collection<Integer> getAutoSupplyItems() {
        return _autoSupplyItems;
    }
}
