package org.l2j.gameserver.model.holders;

import java.util.List;

/**
 * @author Mobius
 */
public class EquipmentUpgradeHolder
{
    private final int _id;
    private final int _requiredItemId;
    private final int _requiredItemEnchant;
    private final List<ItemHolder> _materials;
    private final long _adena;
    private final int _resultItemId;
    private final int _resultItemEnchant;

    public EquipmentUpgradeHolder(int id, int requiredItemId, int requiredItemEnchant, List<ItemHolder> materials, long adena, int resultItemId, int resultItemEnchant)
    {
        _id = id;
        _requiredItemId = requiredItemId;
        _requiredItemEnchant = requiredItemEnchant;
        _materials = materials;
        _adena = adena;
        _resultItemId = resultItemId;
        _resultItemEnchant = resultItemEnchant;
    }

    public int getId()
    {
        return _id;
    }

    public int getRequiredItemId()
    {
        return _requiredItemId;
    }

    public int getRequiredItemEnchant()
    {
        return _requiredItemEnchant;
    }

    public List<ItemHolder> getMaterials()
    {
        return _materials;
    }

    public long getAdena()
    {
        return _adena;
    }

    public int getResultItemId()
    {
        return _resultItemId;
    }

    public int getResultItemEnchant()
    {
        return _resultItemEnchant;
    }
}
