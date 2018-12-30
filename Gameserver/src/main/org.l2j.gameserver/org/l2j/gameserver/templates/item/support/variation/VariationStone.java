package org.l2j.gameserver.templates.item.support.variation;

import io.github.joealisson.primitive.maps.IntObjectMap;
import io.github.joealisson.primitive.maps.impl.HashIntObjectMap;

/**
 * @author Bonux
 */
public class VariationStone
{
    private final int _id;
    private final IntObjectMap<VariationInfo> _variations = new HashIntObjectMap<>();

    public VariationStone(int id)
    {
        _id = id;
    }

    public int getId()
    {
        return _id;
    }

    public void addVariation(VariationInfo variation)
    {
        _variations.put(variation.getId(), variation);
    }

    public VariationInfo getVariation(int id)
    {
        return _variations.get(id);
    }

    public VariationInfo[] getVariations()
    {
        return _variations.values().toArray(new VariationInfo[_variations.size()]);
    }
}