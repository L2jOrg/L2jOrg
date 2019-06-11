package org.l2j.gameserver.network.serverpackets;

import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.SystemMessageId;

/**
 * ConfirmDlg server packet implementation.
 *
 * @author kombat, UnAfraid
 */
public class ConfirmDlg extends AbstractMessagePacket<ConfirmDlg> {
    private int _time;
    private int _requesterId;

    public ConfirmDlg(SystemMessageId smId) {
        super(smId);
    }

    public ConfirmDlg(int id) {
        this(SystemMessageId.getSystemMessageId(id));
    }

    public ConfirmDlg(String text) {
        this(SystemMessageId.S1_3);
        addString(text);
    }

    public ConfirmDlg addTime(int time) {
        _time = time;
        return this;
    }

    public ConfirmDlg addRequesterId(int id) {
        _requesterId = id;
        return this;
    }


    @Override
    protected void writeParamType(int type) {
        writeInt(type);
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.CONFIRM_DLG);

        writeInt(getId());
        writeMe();
        writeInt(_time);
        writeInt(_requesterId);
    }

}
