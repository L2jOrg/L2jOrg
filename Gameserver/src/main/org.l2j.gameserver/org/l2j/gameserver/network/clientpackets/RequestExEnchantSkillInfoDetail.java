package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.enums.SkillEnchantType;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.serverpackets.ExEnchantSkillInfoDetail;

/**
 * @author -Wooden-
 */
public final class RequestExEnchantSkillInfoDetail extends ClientPacket {
    private SkillEnchantType _type;
    private int _skillId;
    private int _skillLvl;
    private int _skillSubLvl;

    @Override
    public void readImpl() {
        _type = SkillEnchantType.values()[readInt()];
        _skillId = readInt();
        _skillLvl = readShort();
        _skillSubLvl = readShort();
    }

    @Override
    public void runImpl() {
        if ((_skillId <= 0) || (_skillLvl <= 0) || (_skillSubLvl < 0)) {
            return;
        }

        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }
        activeChar.sendPacket(new ExEnchantSkillInfoDetail(_type, _skillId, _skillLvl, _skillSubLvl, activeChar));
    }
}
