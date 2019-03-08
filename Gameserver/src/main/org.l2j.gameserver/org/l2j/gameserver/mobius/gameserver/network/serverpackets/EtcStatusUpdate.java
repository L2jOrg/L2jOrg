package org.l2j.gameserver.mobius.gameserver.network.serverpackets;

import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.zone.ZoneId;
import org.l2j.gameserver.mobius.gameserver.network.OutgoingPackets;

/**
 * @author Luca Baldi
 */
public class EtcStatusUpdate implements IClientOutgoingPacket
{
    private final L2PcInstance _activeChar;
    private int _mask;

    public EtcStatusUpdate(L2PcInstance activeChar)
    {
        _activeChar = activeChar;
        _mask = _activeChar.getMessageRefusal() || _activeChar.isChatBanned() || _activeChar.isSilenceMode() ? 1 : 0;
        _mask |= _activeChar.isInsideZone(ZoneId.DANGER_AREA) ? 2 : 0;
        _mask |= _activeChar.hasCharmOfCourage() ? 4 : 0;
    }

    @Override
    public boolean write(PacketWriter packet)
    {
        OutgoingPackets.ETC_STATUS_UPDATE.writeId(packet);

        packet.writeC(_activeChar.getCharges()); // 1-7 increase force, lvl
        packet.writeD(_activeChar.getWeightPenalty()); // 1-4 weight penalty, lvl (1=50%, 2=66.6%, 3=80%, 4=100%)
        packet.writeC(_activeChar.getExpertiseWeaponPenalty()); // Weapon Grade Penalty [1-4]
        packet.writeC(_activeChar.getExpertiseArmorPenalty()); // Armor Grade Penalty [1-4]
        packet.writeC(0); // Death Penalty [1-15, 0 = disabled)], not used anymore in Ertheia
        packet.writeC(_activeChar.getChargedSouls());
        packet.writeC(_mask);
        return true;
    }
}
