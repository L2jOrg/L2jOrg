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
import io.github.joealisson.mmocore.WritablePacket;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;

public abstract class L2GameServerPacket extends WritablePacket<GameClient> implements IBroadcastPacket {
    private static final int IS_AUGMENTED = 1 << 0;
    private static final int IS_ELEMENTED = 1 << 1;
    private static final int HAVE_ENCHANT_OPTIONS = 1 << 2;
    private static final int HAVE_ENSOUL = 1 << 4;

    private static final Logger _log = LoggerFactory.getLogger(L2GameServerPacket.class);

    @Override
    public final boolean write(GameClient client, ByteBuffer buffer) {
        try {
            if (writeOpcodes(buffer)) {
                writeImpl(client, buffer);
                _log.debug("{} {}", toString(), buffer.position());
                return true;
            }
        } catch (Exception e) {
            _log.error("Client: {} - Failed writing: {} - Server Version {}", client, toString(),  GameServer.getInstance().getVersion());
            _log.error(e.getLocalizedMessage(), e);
        }
        return false;
    }

    protected ServerPacketOpcodes getOpcodes() {
        try {
            return ServerPacketOpcodes.valueOf(getClass().getSimpleName());
        } catch (Exception e) {
            _log.error("Cannot find serverpacket opcode: {}!",  toString());
        }
        return null;
    }

    protected boolean writeOpcodes(ByteBuffer buffer) {
        ServerPacketOpcodes opcodes = getOpcodes();
        if (opcodes == null) {
            return false;
        }

        int opcode = opcodes.getId();
        buffer.put((byte)opcode);
        if (opcode == 0xFE) {
            buffer.putShort((short) opcodes.getExId());
        }

        return true;
    }

    protected abstract void writeImpl(GameClient client, ByteBuffer buffer);


    /**
     * Отсылает число позиций + массив
     */
    protected void writeIntList(ByteBuffer buffer, int[] values, boolean sendCount) {
        if (sendCount) {
            buffer.putInt(values.length);
        }
        for (int value : values) {
            buffer.putInt(value);
        }
    }

    protected void writeIntList(ByteBuffer buffer, int[] values) {
        writeIntList(buffer, values, false);
    }

    protected void writeItemInfo(ByteBuffer buffer, ItemInstance item) {
        writeItemInfo(buffer, null, item, item.getCount());
    }

    protected void writeItemInfo(ByteBuffer buffer, Player player, ItemInstance item) {
        writeItemInfo(buffer, player, item, item.getCount());
    }

    protected void writeItemInfo(ByteBuffer buffer, ItemInstance item, long count) {
        writeItemInfo(buffer,null, item, count);
    }

    protected void writeItemInfo(ByteBuffer buffer, Player player, ItemInstance item, long count) {
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

        buffer.put((byte)flags);
        buffer.putInt(item.getObjectId());
        buffer.putInt(item.getItemId());
        buffer.put((byte) (item.isEquipped() ? -1 : item.getEquipSlot()));
        buffer.putLong(count);
        buffer.put((byte)item.getTemplate().getType2());
        buffer.put((byte)item.getCustomType1());
        buffer.putShort((short) (item.isEquipped() ? 1 : 0));
        buffer.putLong(item.getBodyPart());
        buffer.put((byte)item.getFixedEnchantLevel(player));
        buffer.put((byte)item.getCustomType2());
        buffer.putInt(item.getShadowLifeTime());
        buffer.putInt(item.getTemporalLifeTime());

        if (player != null)
            buffer.put((byte) (!item.getTemplate().isBlocked(player, item)  ? 0x01 :  0x00));
        else
            buffer.put((byte)0x01);

        buffer.put((byte) 0x00); // 140 protocol
        buffer.put((byte) 0x00); // 140 protocol

        if ((flags & IS_AUGMENTED) == IS_AUGMENTED) {
            buffer.putInt(item.getVariation1Id());
            buffer.putInt(item.getVariation2Id());
        }

        if ((flags & IS_ELEMENTED) == IS_ELEMENTED) {
            buffer.putShort((short) item.getAttackElement().getId());
            buffer.putShort((short) attackElementValue);
            buffer.putShort((short) defenceFire);
            buffer.putShort((short) defenceWater);
            buffer.putShort((short) defenceWind);
            buffer.putShort((short) defenceEarth);
            buffer.putShort((short) defenceHoly);
            buffer.putShort((short) defenceUnholy);
        }

        if ((flags & HAVE_ENCHANT_OPTIONS) == HAVE_ENCHANT_OPTIONS) {
            buffer.putInt(item.getEnchantOptions()[0]);
            buffer.putInt(item.getEnchantOptions()[1]);
            buffer.putInt(item.getEnchantOptions()[2]);
        }

        if ((flags & HAVE_ENSOUL) == HAVE_ENSOUL) {
            buffer.put((byte)normalEnsouls.length);
            for (Ensoul ensoul : normalEnsouls)
                buffer.putInt(ensoul.getId());

            buffer.put((byte)specialEnsouls.length);
            for (Ensoul ensoul : specialEnsouls)
                buffer.putInt(ensoul.getId());
        }
    }

    protected void writeItemInfo(ByteBuffer buffer, ItemInfo item) {
        writeItemInfo(buffer, item, item.getCount());
    }

    protected void writeItemInfo(ByteBuffer buffer, ItemInfo item, long count) {
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

        buffer.put((byte)flags);
        buffer.putInt(item.getObjectId());
        buffer.putInt(item.getItemId());
        buffer.put((byte) (item.isEquipped() ? -1 : item.getEquipSlot()));
        buffer.putLong(count);
        buffer.put((byte)item.getItem().getType2());
        buffer.put((byte)item.getCustomType1());
        buffer.putShort( (short) (item.isEquipped() ? 1 : 0));
        buffer.putLong(item.getItem().getBodyPart());
        buffer.put((byte)item.getEnchantLevel());
        buffer.put((byte)item.getCustomType2());
        buffer.putInt(item.getShadowLifeTime());
        buffer.putInt(item.getTemporalLifeTime());
        buffer.put((byte) (!item.isBlocked() ? 0x01 : 0x00));
        buffer.put((byte) 0x00); // 140 protocol
        buffer.put((byte) 0x00); // 140 protocol

        if ((flags & IS_AUGMENTED) == IS_AUGMENTED) {
            buffer.putInt(item.getVariation1Id());
            buffer.putInt(item.getVariation2Id());
        }

        if ((flags & IS_ELEMENTED) == IS_ELEMENTED) {
            buffer.putShort((short) item.getAttackElement());
            buffer.putShort((short) attackElementValue);
            buffer.putShort((short) defenceFire);
            buffer.putShort((short) defenceWater);
            buffer.putShort((short) defenceWind);
            buffer.putShort((short) defenceEarth);
            buffer.putShort((short) defenceHoly);
            buffer.putShort((short) defenceUnholy);
        }

        if ((flags & HAVE_ENCHANT_OPTIONS) == HAVE_ENCHANT_OPTIONS) {
            buffer.putInt(item.getEnchantOptions()[0]);
            buffer.putInt(item.getEnchantOptions()[1]);
            buffer.putInt(item.getEnchantOptions()[2]);
        }

        if ((flags & HAVE_ENSOUL) == HAVE_ENSOUL) {
            buffer.put((byte)normalEnsouls.length);
            for (Ensoul ensoul : normalEnsouls)
                buffer.putInt(ensoul.getId());

            buffer.put((byte)specialEnsouls.length);
            for (Ensoul ensoul : specialEnsouls)
                buffer.putInt(ensoul.getId());
        }
    }

    protected void writeCommissionItem(ByteBuffer buffer, CommissionItem item) {
        buffer.putInt(item.getItemId());
        buffer.put((byte)item.getEquipSlot());
        buffer.putLong(item.getCount());
        buffer.putShort((short) item.getItem().getType2()); //??item.getCustomType1()??
        buffer.putLong(item.getItem().getBodyPart());
        buffer.putShort((short) item.getEnchantLevel());
        buffer.putShort((short) item.getCustomType2());
        buffer.putShort((short) item.getAttackElement());
        buffer.putShort((short) item.getAttackElementValue());
        buffer.putShort((short) item.getDefenceFire());
        buffer.putShort((short) item.getDefenceWater());
        buffer.putShort((short) item.getDefenceWind());
        buffer.putShort((short) item.getDefenceEarth());
        buffer.putShort((short) item.getDefenceHoly());
        buffer.putShort((short) item.getDefenceUnholy());
        buffer.putInt(item.getEnchantOptions()[0]);
        buffer.putInt(item.getEnchantOptions()[1]);
        buffer.putInt(item.getEnchantOptions()[2]);
    }

    protected void writeItemElements(ByteBuffer buffer, MultiSellIngredient item) {
        if (item.getItemId() <= 0) {
            writeItemElements(buffer);
            return;
        }

        ItemTemplate i = ItemHolder.getInstance().getTemplate(item.getItemId());
        if (item.getItemAttributes().getValue() > 0) {
            if (i.isWeapon()) {
                Element e = item.getItemAttributes().getElement();
                buffer.putShort((short) e.getId()); // attack element (-1 - none)
                buffer.putShort((short) (item.getItemAttributes().getValue(e) + i.getBaseAttributeValue(e))); // attack element value
                buffer.putShort((short) 0); // водная стихия (fire pdef)
                buffer.putShort((short) 0); // огненная стихия (water pdef)
                buffer.putShort((short) 0); // земляная стихия (wind pdef)
                buffer.putShort((short) 0); // воздушная стихия (earth pdef)
                buffer.putShort((short) 0); // темная стихия (holy pdef)
                buffer.putShort((short) 0); // светлая стихия (dark pdef)
            } else if (i.isArmor()) {
                buffer.putShort((short) -1); // attack element (-1 - none)
                buffer.putShort((short) 0); // attack element value
                for (Element e : Element.VALUES)
                    buffer.putShort((short) (item.getItemAttributes().getValue(e) + i.getBaseAttributeValue(e)));
            } else
                writeItemElements(buffer);
        } else
            writeItemElements(buffer);
    }

    protected void writeItemElements(ByteBuffer buffer) {
        buffer.putShort((short) -1); // attack element (-1 - none)
        buffer.putShort((short) 0x00); // attack element value
        buffer.putShort((short) 0x00); // водная стихия (fire pdef)
        buffer.putShort((short) 0x00); // огненная стихия (water pdef)
        buffer.putShort((short) 0x00); // земляная стихия (wind pdef)
        buffer.putShort((short) 0x00); // воздушная стихия (earth pdef)
        buffer.putShort((short) 0x00); // темная стихия (holy pdef)
        buffer.putShort((short) 0x00); // светлая стихия (dark pdef)
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