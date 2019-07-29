package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.impl.SkillData;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.model.skills.CommonSkill;
import org.l2j.gameserver.model.skills.Skill;
import org.l2j.gameserver.network.SystemMessageId;
import org.l2j.gameserver.network.serverpackets.ActionFailed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class RequestMagicSkillUse extends ClientPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestMagicSkillUse.class);
    private int _magicId;
    private boolean _ctrlPressed;
    private boolean _shiftPressed;

    @Override
    public void readImpl() {
        _magicId = readInt(); // Identifier of the used skill
        _ctrlPressed = readInt() != 0; // True if it's a ForceAttack : Ctrl pressed
        _shiftPressed = readByte() != 0; // True if Shift pressed
    }

    @Override
    public void runImpl() {
        // Get the current Player of the player
        final Player activeChar = client.getPlayer();
        if (activeChar == null) {
            return;
        }

        // Get the level of the used skill
        Skill skill = activeChar.getKnownSkill(_magicId);
        if (skill == null) {
            if ((_magicId == CommonSkill.HAIR_ACCESSORY_SET.getId()) //
                    || ((_magicId > 1565) && (_magicId < 1570))) // subClass change SkillTree
            {
                skill = SkillData.getInstance().getSkill(_magicId, 1);
            } else {
                activeChar.sendPacket(ActionFailed.STATIC_PACKET);
                if (_magicId > 0) {
                    LOGGER.warn("Skill Id " + _magicId + " not found in player: " + activeChar);
                }
                return;
            }
        }

        // Skill is blocked from player use.
        if (skill.isBlockActionUseSkill()) {
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        // Avoid Use of Skills in AirShip.
        if (activeChar.isInAirShip()) {
            activeChar.sendPacket(SystemMessageId.THIS_ACTION_IS_PROHIBITED_WHILE_MOUNTED_OR_ON_AN_AIRSHIP);
            activeChar.sendPacket(ActionFailed.STATIC_PACKET);
            return;
        }

        activeChar.useMagic(skill, null, _ctrlPressed, _shiftPressed);
    }
}
