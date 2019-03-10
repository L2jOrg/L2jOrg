package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.mobius.gameserver.network.L2GameClient;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

import java.nio.ByteBuffer;

/**
 * @author Luca Baldi
 */
public class EtcStatusUpdate extends IClientOutgoingPacket {
    private final L2PcInstance _activeChar;
    private int _mask;

    public EtcStatusUpdate(L2PcInstance activeChar) {
        _activeChar = activeChar;
        _mask = _activeChar.getMessageRefusal() || _activeChar.isChatBanned() || _activeChar.isSilenceMode() ? 1 : 0;
        _mask |= _activeChar.isInsideZone(ZoneId.DANGER_AREA) ? 2 : 0;
        _mask |= _activeChar.hasCharmOfCourage() ? 4 : 0;
    }

    @Override
    public void writeImpl(L2GameClient client, ByteBuffer packet) {
        OutgoingPackets.ETC_STATUS_UPDATE.writeId(packet);

        packet.put((byte) _activeChar.getCharges()); // 1-7 increase force, lvl
        packet.putInt(_activeChar.getWeightPenalty()); // 1-4 weight penalty, lvl (1=50%, 2=66.6%, 3=80%, 4=100%)
        packet.put((byte) _activeChar.getExpertiseWeaponPenalty()); // Weapon Grade Penalty [1-4]
        packet.put((byte) _activeChar.getExpertiseArmorPenalty()); // Armor Grade Penalty [1-4]
        packet.put((byte) 0); // Death Penalty [1-15, 0 = disabled)], not used anymore in Ertheia
        packet.put((byte) _activeChar.getChargedSouls());
        packet.put((byte) _mask);
    }
}
