package org.l2j.gameserver.enums;

import org.l2j.gameserver.model.interfaces.IUpdateTypeComponent;

import java.util.EnumSet;

/**
 * @author UnAfraid
 * @author JoeAlisson
 */
public enum InventorySlot implements IUpdateTypeComponent {
    PENDANT,
    RIGHT_EAR,
    LEFT_EAR,
    NECK,
    RIGHT_FINGER,
    LEFT_FINGER,
    HEAD,
    RIGHT_HAND,
    LEFT_HAND,
    GLOVES,
    CHEST,
    LEGS,
    FEET,
    CLOAK,
    TWO_HAND,
    HAIR,
    HAIR2,
    RIGHT_BRACELET,
    LEFT_BRACELET,
    AGATHION1,
    AGATHION2,
    AGATHION3,
    AGATHION4,
    AGATHION5,
    TALISMAN1,
    TALISMAN2,
    TALISMAN3,
    TALISMAN4,
    TALISMAN5,
    TALISMAN6,
    BELT,
    BROOCH,
    BROOCH_JEWEL1,
    BROOCH_JEWEL2,
    BROOCH_JEWEL3,
    BROOCH_JEWEL4,
    BROOCH_JEWEL5,
    BROOCH_JEWEL6,
    ARTIFACT_BOOK,
    ARTIFACT1,
    ARTIFACT2,
    ARTIFACT3,
    ARTIFACT4,
    ARTIFACT5,
    ARTIFACT6,
    ARTIFACT7,
    ARTIFACT8,
    ARTIFACT9,
    ARTIFACT10,
    ARTIFACT11,
    ARTIFACT12,
    ARTIFACT13,
    ARTIFACT14,
    ARTIFACT15,
    ARTIFACT16,
    ARTIFACT17,
    ARTIFACT18,
    ARTIFACT19,
    ARTIFACT20,
    ARTIFACT21;

    private static final EnumSet<InventorySlot> accessories = EnumSet.of(LEFT_FINGER, RIGHT_FINGER, LEFT_EAR, RIGHT_EAR, NECK);
    private static final EnumSet<InventorySlot> armors = EnumSet.of(CHEST, LEGS, HEAD, FEET, GLOVES, PENDANT, CLOAK, HAIR);
    private static final EnumSet<InventorySlot> balanceArtifacts = EnumSet.range(ARTIFACT1, ARTIFACT12);
    private static final EnumSet<InventorySlot> spiritArtifacts = EnumSet.range(ARTIFACT13, ARTIFACT15);
    private static final EnumSet<InventorySlot> protectionArtifacts = EnumSet.range(ARTIFACT16, ARTIFACT18);
    private static final EnumSet<InventorySlot> supportArtifact = EnumSet.range(ARTIFACT19, ARTIFACT21);
    private static final EnumSet<InventorySlot> agathions = EnumSet.range(AGATHION1, AGATHION5);
    private static final EnumSet<InventorySlot> broochesJewel = EnumSet.range(BROOCH_JEWEL1, BROOCH_JEWEL6);
    private static final EnumSet<InventorySlot> talismans = EnumSet.range(TALISMAN1, TALISMAN6);
    private static final EnumSet<InventorySlot> armorset = EnumSet.of(CHEST, LEGS, HEAD, GLOVES, FEET);
    public static final int TOTAL_SLOTS = 60;

    public static final InventorySlot[] CACHE = values();

    public int getId() {
        return ordinal();
    }

    @Override
    public int getMask() {
        return ordinal();
    }

    public static InventorySlot fromId(int id) {
        if(id < 0 || id > ARTIFACT21.ordinal()) {
            return null;
        }
        return CACHE[id];
    }

    public static InventorySlot[] cachedValues() {
        return CACHE;
    }

    public static EnumSet<InventorySlot> accessories() {
        return accessories;
    }

    public static EnumSet<InventorySlot> armors() {
        return armors;
    }

    public static EnumSet<InventorySlot> balanceArtifacts() {
        return balanceArtifacts;
    }

    public static EnumSet<InventorySlot> spiritArtifacts() {
        return spiritArtifacts;
    }

    public static EnumSet<InventorySlot> protectionArtifacts() {
        return protectionArtifacts;
    }

    public static EnumSet<InventorySlot> supportArtifact() {
        return supportArtifact;
    }

    public static EnumSet<InventorySlot> agathions() {
        return agathions;
    }

    public static EnumSet<InventorySlot> brochesJewel() {
        return broochesJewel;
    }

    public static EnumSet<InventorySlot> talismans() {
        return talismans;
    }

    public static EnumSet<InventorySlot> armorset() {
        return armorset;
    }
}
