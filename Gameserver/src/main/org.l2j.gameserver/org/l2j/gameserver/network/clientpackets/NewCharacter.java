package org.l2j.gameserver.network.clientpackets;

import org.l2j.gameserver.data.xml.impl.PlayerTemplateData;
import org.l2j.gameserver.model.base.ClassId;
import org.l2j.gameserver.network.serverpackets.NewCharacterSuccess;

import java.nio.ByteBuffer;

/**
 * @author Zoey76
 */
public final class NewCharacter extends IClientIncomingPacket {
    @Override
    public void readImpl(ByteBuffer packet) {

    }

    @Override
    public void runImpl() {
        final NewCharacterSuccess ct = new NewCharacterSuccess();
        ct.addChar(PlayerTemplateData.getInstance().getTemplate(ClassId.FIGHTER)); // Human Figther
        ct.addChar(PlayerTemplateData.getInstance().getTemplate(ClassId.MAGE)); // Human Mystic
        ct.addChar(PlayerTemplateData.getInstance().getTemplate(ClassId.ELVEN_FIGHTER)); // Elven Fighter
        ct.addChar(PlayerTemplateData.getInstance().getTemplate(ClassId.ELVEN_MAGE)); // Elven Mystic
        ct.addChar(PlayerTemplateData.getInstance().getTemplate(ClassId.DARK_FIGHTER)); // Dark Fighter
        ct.addChar(PlayerTemplateData.getInstance().getTemplate(ClassId.DARK_MAGE)); // Dark Mystic
        ct.addChar(PlayerTemplateData.getInstance().getTemplate(ClassId.ORC_FIGHTER)); // Orc Fighter
        ct.addChar(PlayerTemplateData.getInstance().getTemplate(ClassId.ORC_MAGE)); // Orc Mystic
        ct.addChar(PlayerTemplateData.getInstance().getTemplate(ClassId.DWARVEN_FIGHTER)); // Dwarf Fighter
        ct.addChar(PlayerTemplateData.getInstance().getTemplate(ClassId.MALE_SOLDIER)); // Male Kamael Soldier
        ct.addChar(PlayerTemplateData.getInstance().getTemplate(ClassId.FEMALE_SOLDIER)); // Female Kamael Soldier
        ct.addChar(PlayerTemplateData.getInstance().getTemplate(ClassId.ERTHEIA_FIGHTER)); // Ertheia Fighter
        ct.addChar(PlayerTemplateData.getInstance().getTemplate(ClassId.ERTHEIA_WIZARD)); // Ertheia Wizard
        client.sendPacket(ct);
    }
}
