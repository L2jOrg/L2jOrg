package org.l2j.gameserver.network.l2.s2c;

import org.l2j.gameserver.GameServer;
import org.l2j.gameserver.data.xml.holder.ItemHolder;
import org.l2j.gameserver.model.Player;
import org.l2j.gameserver.model.base.Element;
import org.l2j.gameserver.model.base.MultiSellIngredient;
import org.l2j.gameserver.model.items.CommissionItem;
import org.l2j.gameserver.model.items.ItemInfo;
import org.l2j.gameserver.model.items.ItemInstance;
import org.l2j.gameserver.network.l2.GameClient;
import org.l2j.gameserver.network.l2.ServerPacketOpcodes;
import org.l2j.gameserver.network.l2.components.IBroadcastPacket;
import org.l2j.gameserver.network.l2.s2c.updatetype.IUpdateTypeComponent;
import org.l2j.gameserver.templates.item.ItemTemplate;
import org.l2j.gameserver.templates.item.support.Ensoul;
import org.l2j.mmocore.WritablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class L2GameServerPacket extends WritablePacket<GameClient> implements IBroadcastPacket {
    private static final int IS_AUGMENTED = 1 << 0;
    private static final int IS_ELEMENTED = 1 << 1;
    private static final int HAVE_ENCHANT_OPTIONS = 1 << 2;
    private static final int HAVE_ENSOUL = 1 << 4;

    private static final Logger _log = LoggerFactory.getLogger(L2GameServerPacket.class);

    @Override
    public final boolean write() {
        try {
            if (writeOpcodes()) {
                writeImpl();
                return true;
            }
        } catch (Exception e) {
            _log.error("Client: " + getClient() + " - Failed writing: " + getType() + " - Server Version: " + GameServer.getInstance().getVersion(), e);
        }
        return false;
    }

    protected ServerPacketOpcodes getOpcodes() {
        try {
            return ServerPacketOpcodes.valueOf(getClass().getSimpleName());
        } catch (Exception e) {
            _log.error("Cannot find serverpacket opcode: " + getClass().getSimpleName() + "!");
        }
        return null;
    }

    protected boolean writeOpcodes() {
        ServerPacketOpcodes opcodes = getOpcodes();
        if (opcodes == null) {
            return false;
        }

        int opcode = opcodes.getId();
        writeByte(opcode);
        if (opcode == 0xFE) {
            writeShort(opcodes.getExId());
        }

        return true;
    }

    protected abstract void writeImpl();

    @Override
    protected int packetSize() {
        return getOpcodes().getId() == 0xFE ? 3 : 1;
    }

    /**
     * Отсылает число позиций + массив
     */
    protected void writeDD(int[] values, boolean sendCount) {
        if (sendCount) {
            writeInt(values.length);
        }
        for (int value : values) {
            writeInt(value);
        }
    }

    protected void writeDD(int[] values) {
        writeDD(values, false);
    }

    protected void writeItemInfo(ItemInstance item) {
        writeItemInfo(null, item, item.getCount());
    }

    protected void writeItemInfo(Player player, ItemInstance item) {
        writeItemInfo(player, item, item.getCount());
    }

    protected void writeItemInfo(ItemInstance item, long count) {
        writeItemInfo(null, item, count);
    }

    protected void writeItemInfo(Player player, ItemInstance item, long count) {
        int flags = 0;

        if (item.isAugmented())
            flags |= IS_AUGMENTED;

        int attackElementValue = item.getAttackElementValue();
        int defenceFire = item.getDefenceFire();
        int defenceWater = item.getDefenceWater();
        int defenceWind = item.getDefenceWind();
        int defenceEarth = item.getDefenceEarth();
        int defenceHoly = item.getDefenceHoly();
        int defenceUnholy = item.getDefenceUnholy();
        if (attackElementValue > 0 || defenceFire > 0 || defenceWater > 0 || defenceWind > 0 || defenceEarth > 0 || defenceHoly > 0 || defenceUnholy > 0)
            flags |= IS_ELEMENTED;

        for (int enchantOption : item.getEnchantOptions()) {
            if (enchantOption > 0) {
                flags |= HAVE_ENCHANT_OPTIONS;
                break;
            }
        }

        Ensoul[] normalEnsouls = item.getNormalEnsouls();
        Ensoul[] specialEnsouls = item.getSpecialEnsouls();
        if (normalEnsouls.length > 0 || specialEnsouls.length > 0)
            flags |= HAVE_ENSOUL;

        writeByte(flags);
        writeInt(item.getObjectId());
        writeInt(item.getItemId());
        writeByte(item.isEquipped() ? -1 : item.getEquipSlot());
        writeLong(count);
        writeByte(item.getTemplate().getType2());
        writeByte(item.getCustomType1());
        writeShort(item.isEquipped() ? 1 : 0);
        writeLong(item.getBodyPart());
        writeByte(item.getFixedEnchantLevel(player));
        writeByte(item.getCustomType2());
        writeInt(item.getShadowLifeTime());
        writeInt(item.getTemporalLifeTime());

        if (player != null)
            writeByte(!item.getTemplate().isBlocked(player, item));
        else
            writeByte(0x01);

        if ((flags & IS_AUGMENTED) == IS_AUGMENTED) {
            writeInt(item.getVariation1Id());
            writeInt(item.getVariation2Id());
        }

        if ((flags & IS_ELEMENTED) == IS_ELEMENTED) {
            writeShort(item.getAttackElement().getId());
            writeShort(attackElementValue);
            writeShort(defenceFire);
            writeShort(defenceWater);
            writeShort(defenceWind);
            writeShort(defenceEarth);
            writeShort(defenceHoly);
            writeShort(defenceUnholy);
        }

        if ((flags & HAVE_ENCHANT_OPTIONS) == HAVE_ENCHANT_OPTIONS) {
            writeInt(item.getEnchantOptions()[0]);
            writeInt(item.getEnchantOptions()[1]);
            writeInt(item.getEnchantOptions()[2]);
        }

        if ((flags & HAVE_ENSOUL) == HAVE_ENSOUL) {
            writeByte(normalEnsouls.length);
            for (Ensoul ensoul : normalEnsouls)
                writeInt(ensoul.getId());

            writeByte(specialEnsouls.length);
            for (Ensoul ensoul : specialEnsouls)
                writeInt(ensoul.getId());
        }
    }

    protected void writeItemInfo(ItemInfo item) {
        writeItemInfo(item, item.getCount());
    }

    protected void writeItemInfo(ItemInfo item, long count) {
        int flags = 0;

        if (item.getVariation1Id() > 0 || item.getVariation2Id() > 0)
            flags |= IS_AUGMENTED;

        int attackElementValue = item.getAttackElementValue();
        int defenceFire = item.getDefenceFire();
        int defenceWater = item.getDefenceWater();
        int defenceWind = item.getDefenceWind();
        int defenceEarth = item.getDefenceEarth();
        int defenceHoly = item.getDefenceHoly();
        int defenceUnholy = item.getDefenceUnholy();
        if (attackElementValue > 0 || defenceFire > 0 || defenceWater > 0 || defenceWind > 0 || defenceEarth > 0 || defenceHoly > 0 || defenceUnholy > 0)
            flags |= IS_ELEMENTED;

        for (int enchantOption : item.getEnchantOptions()) {
            if (enchantOption > 0) {
                flags |= HAVE_ENCHANT_OPTIONS;
                break;
            }
        }

        Ensoul[] normalEnsouls = item.getNormalEnsouls();
        Ensoul[] specialEnsouls = item.getSpecialEnsouls();
        if (normalEnsouls.length > 0 || specialEnsouls.length > 0)
            flags |= HAVE_ENSOUL;

        writeByte(flags);
        writeInt(item.getObjectId());
        writeInt(item.getItemId());
        writeByte(item.isEquipped() ? -1 : item.getEquipSlot());
        writeLong(count);
        writeByte(item.getItem().getType2());
        writeByte(item.getCustomType1());
        writeShort(item.isEquipped() ? 1 : 0);
        writeLong(item.getItem().getBodyPart());
        writeByte(item.getEnchantLevel());
        writeByte(item.getCustomType2());
        writeInt(item.getShadowLifeTime());
        writeInt(item.getTemporalLifeTime());
        writeByte(!item.isBlocked());

        if ((flags & IS_AUGMENTED) == IS_AUGMENTED) {
            writeInt(item.getVariation1Id());
            writeInt(item.getVariation2Id());
        }

        if ((flags & IS_ELEMENTED) == IS_ELEMENTED) {
            writeShort(item.getAttackElement());
            writeShort(attackElementValue);
            writeShort(defenceFire);
            writeShort(defenceWater);
            writeShort(defenceWind);
            writeShort(defenceEarth);
            writeShort(defenceHoly);
            writeShort(defenceUnholy);
        }

        if ((flags & HAVE_ENCHANT_OPTIONS) == HAVE_ENCHANT_OPTIONS) {
            writeInt(item.getEnchantOptions()[0]);
            writeInt(item.getEnchantOptions()[1]);
            writeInt(item.getEnchantOptions()[2]);
        }

        if ((flags & HAVE_ENSOUL) == HAVE_ENSOUL) {
            writeByte(normalEnsouls.length);
            for (Ensoul ensoul : normalEnsouls)
                writeInt(ensoul.getId());

            writeByte(specialEnsouls.length);
            for (Ensoul ensoul : specialEnsouls)
                writeInt(ensoul.getId());
        }
    }

    protected void writeCommissionItem(CommissionItem item) {
        writeInt(item.getItemId());
        writeByte(item.getEquipSlot());
        writeLong(item.getCount());
        writeShort(item.getItem().getType2()); //??item.getCustomType1()??
        writeLong(item.getItem().getBodyPart());
        writeShort(item.getEnchantLevel());
        writeShort(item.getCustomType2());
        writeShort(item.getAttackElement());
        writeShort(item.getAttackElementValue());
        writeShort(item.getDefenceFire());
        writeShort(item.getDefenceWater());
        writeShort(item.getDefenceWind());
        writeShort(item.getDefenceEarth());
        writeShort(item.getDefenceHoly());
        writeShort(item.getDefenceUnholy());
        writeInt(item.getEnchantOptions()[0]);
        writeInt(item.getEnchantOptions()[1]);
        writeInt(item.getEnchantOptions()[2]);
    }

    protected void writeItemElements(MultiSellIngredient item) {
        if (item.getItemId() <= 0) {
            writeItemElements();
            return;
        }

        ItemTemplate i = ItemHolder.getInstance().getTemplate(item.getItemId());
        if (item.getItemAttributes().getValue() > 0) {
            if (i.isWeapon()) {
                Element e = item.getItemAttributes().getElement();
                writeShort(e.getId()); // attack element (-1 - none)
                writeShort(item.getItemAttributes().getValue(e) + i.getBaseAttributeValue(e)); // attack element value
                writeShort(0); // водная стихия (fire pdef)
                writeShort(0); // огненная стихия (water pdef)
                writeShort(0); // земляная стихия (wind pdef)
                writeShort(0); // воздушная стихия (earth pdef)
                writeShort(0); // темная стихия (holy pdef)
                writeShort(0); // светлая стихия (dark pdef)
            } else if (i.isArmor()) {
                writeShort(-1); // attack element (-1 - none)
                writeShort(0); // attack element value
                for (Element e : Element.VALUES)
                    writeShort(item.getItemAttributes().getValue(e) + i.getBaseAttributeValue(e));
            } else
                writeItemElements();
        } else
            writeItemElements();
    }

    protected void writeItemElements() {
        writeShort(-1); // attack element (-1 - none)
        writeShort(0x00); // attack element value
        writeShort(0x00); // водная стихия (fire pdef)
        writeShort(0x00); // огненная стихия (water pdef)
        writeShort(0x00); // земляная стихия (wind pdef)
        writeShort(0x00); // воздушная стихия (earth pdef)
        writeShort(0x00); // темная стихия (holy pdef)
        writeShort(0x00); // светлая стихия (dark pdef)
    }

    public String getType() {
        return "[S] " + getClass().getSimpleName();
    }

    public L2GameServerPacket packet(Player player) {
        return this;
    }

    /**
     * @param masks
     * @param type
     * @return {@code true} if the mask contains the current update component type
     */
    protected static boolean containsMask(int masks, IUpdateTypeComponent type) {
        return (masks & type.getMask()) == type.getMask();
    }
}