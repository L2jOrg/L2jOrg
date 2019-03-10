package org.l2j.gameserver.mobius.gameserver.network.clientpackets;

import org.l2j.gameserver.mobius.gameserver.data.xml.impl.BeautyShopData;
import org.l2j.gameserver.mobius.gameserver.model.actor.instance.L2PcInstance;
import org.l2j.gameserver.mobius.gameserver.model.beautyshop.BeautyData;
import org.l2j.gameserver.mobius.gameserver.model.beautyshop.BeautyItem;
import org.l2j.gameserver.mobius.gameserver.network.serverpackets.ExResponseBeautyRegistReset;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class RequestShowResetShopList extends IClientIncomingPacket {
    private int _hairId;
    private int _faceId;
    private int _colorId;

    @Override
    public void readImpl(ByteBuffer packet) {
        _hairId = packet.getInt();
        _faceId = packet.getInt();
        _colorId = packet.getInt();
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();
        if (player == null) {
            return;
        }

        final BeautyData beautyData = BeautyShopData.getInstance().getBeautyData(player.getRace(), player.getAppearance().getSexType());
        int requiredAdena = 0;

        if (_hairId > 0) {
            final BeautyItem hair = beautyData.getHairList().get(_hairId);
            if (hair == null) {
                player.sendPacket(new ExResponseBeautyRegistReset(player, ExResponseBeautyRegistReset.RESTORE, ExResponseBeautyRegistReset.FAILURE));
                return;
            }

            requiredAdena += hair.getResetAdena();

            if (_colorId > 0) {
                final BeautyItem color = hair.getColors().get(_colorId);
                if (color == null) {
                    player.sendPacket(new ExResponseBeautyRegistReset(player, ExResponseBeautyRegistReset.RESTORE, ExResponseBeautyRegistReset.FAILURE));
                    return;
                }

                requiredAdena += color.getResetAdena();
            }
        }

        if (_faceId > 0) {
            final BeautyItem face = beautyData.getFaceList().get(_faceId);
            if (face == null) {
                player.sendPacket(new ExResponseBeautyRegistReset(player, ExResponseBeautyRegistReset.RESTORE, ExResponseBeautyRegistReset.FAILURE));
                return;
            }

            requiredAdena += face.getResetAdena();
        }

        if ((player.getAdena() < requiredAdena)) {
            player.sendPacket(new ExResponseBeautyRegistReset(player, ExResponseBeautyRegistReset.RESTORE, ExResponseBeautyRegistReset.FAILURE));
            return;
        }

        if (requiredAdena > 0) {
            if (!player.reduceAdena(getClass().getSimpleName(), requiredAdena, null, true)) {
                player.sendPacket(new ExResponseBeautyRegistReset(player, ExResponseBeautyRegistReset.RESTORE, ExResponseBeautyRegistReset.FAILURE));
                return;
            }
        }

        player.getVariables().remove("visualHairId");
        player.getVariables().remove("visualHairColorId");
        player.getVariables().remove("visualFaceId");

        player.sendPacket(new ExResponseBeautyRegistReset(player, ExResponseBeautyRegistReset.RESTORE, ExResponseBeautyRegistReset.SUCCESS));
    }
}