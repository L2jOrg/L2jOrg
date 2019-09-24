package org.l2j.gameserver.enums;

/**
 * @author Mobius
 * @author JoeAlisson
 */
public enum BroochJewel {
    RUBY_LV1(90328, 17814, 0.01),
    RUBY_LV2(90329, 17814, 0.035),
    RUBY_LV3(90330, 17815, 0.075),
    RUBY_LV4(90331, 17816, 0.125),
    RUBY_LV5(90332, 17817, 0.2),
    SHAPPHIRE_LV1(90333, 17818, 0.01),
    SHAPPHIRE_LV2(90334, 17818, 0.035),
    SHAPPHIRE_LV3(90335, 17819, 0.075),
    SHAPPHIRE_LV4(90336, 17820, 0.125),
    SHAPPHIRE_LV5(90337, 17821, 0.2);

    private int _itemId;
    private int _effectId;
    private double _bonus;

    BroochJewel(int itemId, int effectId, double bonus) {
        _itemId = itemId;
        _effectId = effectId;
        _bonus = bonus;
    }

    public int getItemId() {
        return _itemId;
    }

    public int getEffectId() {
        return _effectId;
    }

    public double getBonus() {
        return _bonus;
    }
}
