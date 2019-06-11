package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.instancemanager.CastleManager;
import org.l2j.gameserver.instancemanager.FortManager;
import org.l2j.gameserver.model.L2Clan;
import org.l2j.gameserver.model.L2SiegeClan;
import org.l2j.gameserver.model.actor.L2Character;
import org.l2j.gameserver.model.entity.Castle;
import org.l2j.gameserver.model.entity.Fort;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author UnAfraid, Nos
 */
public class Die extends IClientOutgoingPacket {
    private final int _objectId;
    private final boolean _isSweepable;
    private boolean _toVillage;
    private boolean _toClanHall;
    private boolean _toCastle;
    private boolean _toOutpost;
    private boolean _useFeather;
    private boolean _toFortress;
    private boolean _hideAnimation;
    private List<Integer> _items = null;
    private boolean _itemsEnabled;

    public Die(L2Character activeChar) {
        _objectId = activeChar.getObjectId();
        if (activeChar.isPlayer()) {
            final L2Clan clan = activeChar.getActingPlayer().getClan();
            boolean isInCastleDefense = false;
            boolean isInFortDefense = false;

            L2SiegeClan siegeClan = null;
            final Castle castle = CastleManager.getInstance().getCastle(activeChar);
            final Fort fort = FortManager.getInstance().getFort(activeChar);
            if ((castle != null) && castle.getSiege().isInProgress()) {
                siegeClan = castle.getSiege().getAttackerClan(clan);
                isInCastleDefense = (siegeClan == null) && castle.getSiege().checkIsDefender(clan);
            } else if ((fort != null) && fort.getSiege().isInProgress()) {
                siegeClan = fort.getSiege().getAttackerClan(clan);
                isInFortDefense = (siegeClan == null) && fort.getSiege().checkIsDefender(clan);
            }

            _toVillage = activeChar.canRevive() && !activeChar.isPendingRevive();
            _toClanHall = (clan != null) && (clan.getHideoutId() > 0);
            _toCastle = ((clan != null) && (clan.getCastleId() > 0)) || isInCastleDefense;
            _toOutpost = ((siegeClan != null) && !isInCastleDefense && !isInFortDefense && !siegeClan.getFlag().isEmpty());
            _useFeather = activeChar.getAccessLevel().allowFixedRes() || activeChar.getInventory().haveItemForSelfResurrection();
            _toFortress = ((clan != null) && (clan.getFortId() > 0)) || isInFortDefense;
        }

        _isSweepable = activeChar.isAttackable() && activeChar.isSweepActive();
    }

    public void setHideAnimation(boolean val) {
        _hideAnimation = val;
    }

    public void addItem(int itemId) {
        if (_items == null) {
            _items = new ArrayList<>(8);
        }

        if (_items.size() < 8) {
            _items.add(itemId);
        } else {
            throw new IndexOutOfBoundsException("Die packet doesn't support more then 8 items!");
        }
    }

    public List<Integer> getItems() {
        return _items != null ? _items : Collections.emptyList();
    }

    public void setItemsEnabled(boolean val) {
        _itemsEnabled = val;
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.DIE);

        writeInt(_objectId);
        writeInt(_toVillage ? 0x01 : 0x00);
        writeInt(_toClanHall ? 0x01 : 0x00);
        writeInt(_toCastle ? 0x01 : 0x00);
        writeInt(_toOutpost ? 0x01 : 0x00);
        writeInt(_isSweepable ? 0x01 : 0x00);
        writeInt(_useFeather ? 0x01 : 0x00);
        writeInt(_toFortress ? 0x01 : 0x00);
        writeInt(0x00); // Disables use Feather button for X seconds
        writeInt(0x00); // Adventure's Song
        writeByte((byte) (_hideAnimation ? 0x01 : 0x00));

        writeInt(_itemsEnabled ? 0x01 : 0x00);
        writeInt(getItems().size());
        getItems().forEach(this::writeInt);
    }

}
