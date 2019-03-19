package org.l2j.gameserver.model.options;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;


/**
 * @author Pere
 */
public final class Variation {
    private static final Logger LOGGER = LoggerFactory.getLogger(Variation.class.getSimpleName());

    private final int _mineralId;
    private final Map<VariationWeaponType, OptionDataGroup[]> _effects = new HashMap<>();

    public Variation(int mineralId) {
        _mineralId = mineralId;
    }

    public int getMineralId() {
        return _mineralId;
    }

    public void setEffectGroup(VariationWeaponType type, int order, OptionDataGroup group) {
        final OptionDataGroup[] effects = _effects.computeIfAbsent(type, k -> new OptionDataGroup[2]);
        effects[order] = group;
    }

    public Options getRandomEffect(VariationWeaponType type, int order) {
        OptionDataGroup[] effects = _effects.get(type);
        if ((effects == null) || (effects[order] == null)) {
            LOGGER.warn("Null effect: " + type + ", " + order);
            return null;
        }

        return effects[order].getRandomEffect();
    }
}