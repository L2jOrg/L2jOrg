package org.l2j.gameserver.model.items.type;

import java.util.function.Consumer;

/**
 * Crystal Type enumerated.
 *
 * @author Adry_85
 */
public enum CrystalType {
    NONE(0, 0, 0, 0),
    D(1, 1458, 11, 90),
    C(2, 1459, 6, 45),
    B(3, 1460, 11, 67),
    A(4, 1461, 20, 145),
    S(5, 1462, 25, 250),
    EVENT(11, 0, 0, 0);

    private final int id;
    private final int crystalId;
    private final int crystalEnchantBonusArmor;
    private final int crystalEnchantBonusWeapon;

    private static final CrystalType[] CACHED = values();

    CrystalType(int id, int crystalId, int crystalEnchantBonusArmor, int crystalEnchantBonusWeapon) {
        this.id = id;
        this.crystalId = crystalId;
        this.crystalEnchantBonusArmor = crystalEnchantBonusArmor;
        this.crystalEnchantBonusWeapon = crystalEnchantBonusWeapon;
    }

    /**
     * Gets the crystal type ID.
     *
     * @return the crystal type ID
     */
    public int getId() {
        return id;
    }

    /**
     * Gets the item ID of the crystal.
     *
     * @return the item ID of the crystal
     */
    public int getCrystalId() {
        return crystalId;
    }

    public int getCrystalEnchantBonusArmor() {
        return crystalEnchantBonusArmor;
    }

    public int getCrystalEnchantBonusWeapon() {
        return crystalEnchantBonusWeapon;
    }

    public static void forEach(Consumer<CrystalType> action) {
        for (CrystalType crystalType : CACHED) {
            action.accept(crystalType);
        }
    }

}
