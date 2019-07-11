package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.sql.impl.PetNameTable;
import org.l2j.gameserver.model.actor.L2Summon;
import org.l2j.gameserver.model.actor.instance.Player;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * This class ...
 *
 * @version $Revision: 1.3.4.4 $ $Date: 2005/04/06 16:13:48 $
 */
public final class RequestChangePetName extends ClientPacket {
    private String _name;

    @Override
    public void readImpl() {
        _name = readString();
    }

    @Override
    public void runImpl() {
        final Player activeChar = client.getActiveChar();
        if (activeChar == null) {
            return;
        }

        final L2Summon pet = activeChar.getPet();
        if (pet == null) {
            return;
        }

        if (!pet.isPet()) {
            activeChar.sendPacket(SystemMessageId.YOU_DO_NOT_HAVE_A_PET);
            return;
        }

        if (pet.getName() != null) {
            activeChar.sendPacket(SystemMessageId.YOU_CANNOT_SET_THE_NAME_OF_THE_PET);
            return;
        }

        if (PetNameTable.getInstance().doesPetNameExist(_name, pet.getTemplate().getId())) {
            activeChar.sendPacket(SystemMessageId.THIS_IS_ALREADY_IN_USE_BY_ANOTHER_PET);
            return;
        }

        if ((_name.length() < 3) || (_name.length() > 16)) {
            // activeChar.sendPacket(SystemMessageId.YOUR_PET_S_NAME_CAN_BE_UP_TO_8_CHARACTERS_IN_LENGTH);
            activeChar.sendMessage("Your pet's name can be up to 16 characters in length.");
            return;
        }

        if (!PetNameTable.getInstance().isValidPetName(_name)) {
            activeChar.sendPacket(SystemMessageId.AN_INVALID_CHARACTER_IS_INCLUDED_IN_THE_PET_S_NAME);
            return;
        }

        pet.setName(_name);
        pet.updateAndBroadcastStatus(1);
    }
}
