package org.l2j.gameserver.network.clientpackets;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.model.actor.instance.L2PcInstance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.ByteBuffer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * @author Plim
 */
public class RequestPetitionFeedback extends IClientIncomingPacket {
    private static final Logger LOGGER = LoggerFactory.getLogger(RequestPetitionFeedback.class);
    private static final String INSERT_FEEDBACK = "INSERT INTO petition_feedback VALUES (?,?,?,?,?)";

    // cdds
    // private int _unknown;
    private int _rate; // 4=VeryGood, 3=Good, 2=Fair, 1=Poor, 0=VeryPoor
    private String _message;

    @Override
    public void readImpl() {
        // _unknown =
        readInt(); // unknown
        _rate = readInt();
        _message = readString();
    }

    @Override
    public void runImpl() {
        final L2PcInstance player = client.getActiveChar();

        if ((player == null) || (player.getLastPetitionGmName() == null)) {
            return;
        }

        if ((_rate > 4) || (_rate < 0)) {
            return;
        }

        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement statement = con.prepareStatement(INSERT_FEEDBACK)) {
            statement.setString(1, player.getName());
            statement.setString(2, player.getLastPetitionGmName());
            statement.setInt(3, _rate);
            statement.setString(4, _message);
            statement.setLong(5, System.currentTimeMillis());
            statement.execute();
        } catch (SQLException e) {
            LOGGER.error("Error while saving petition feedback");
        }
    }

}
