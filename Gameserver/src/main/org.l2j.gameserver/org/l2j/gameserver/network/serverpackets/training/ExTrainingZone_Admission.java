package org.l2j.gameserver.network.serverpackets.training;

import org.l2j.gameserver.Config;
import org.l2j.gameserver.data.xml.impl.ExperienceData;
import org.l2j.gameserver.network.L2GameClient;
import org.l2j.gameserver.network.OutgoingPackets;
import org.l2j.gameserver.network.serverpackets.IClientOutgoingPacket;

import java.nio.ByteBuffer;

/**
 * @author Sdw
 */
public class ExTrainingZone_Admission extends IClientOutgoingPacket {
    private final long _timeElapsed;
    private final long _timeRemaining;
    private final double _maxExp;
    private final double _maxSp;

    public ExTrainingZone_Admission(int level, long timeElapsed, long timeRemaing) {
        _timeElapsed = timeElapsed;
        _timeRemaining = timeRemaing;
        _maxExp = Config.TRAINING_CAMP_EXP_MULTIPLIER * ((ExperienceData.getInstance().getExpForLevel(level) * ExperienceData.getInstance().getTrainingRate(level)) / Config.TRAINING_CAMP_MAX_DURATION);
        _maxSp = Config.TRAINING_CAMP_SP_MULTIPLIER * (_maxExp / 250d);
    }

    @Override
    public void writeImpl(L2GameClient client) {
        writeId(OutgoingPackets.EX_TRAINING_ZONE_ADMISSION);
        writeInt((int) _timeElapsed); // Training time elapsed in minutes, max : 600 - 10hr RU / 300 - 5hr NA
        writeInt((int) _timeRemaining); // Time remaining in seconds, max : 36000 - 10hr RU / 18000 - 5hr NA
        writeDouble(_maxExp); // Training time elapsed in minutes * this value = acquired exp IN GAME DOESN'T SEEM LIKE THE FIELD IS LIMITED
        writeDouble(_maxSp); // Training time elapsed in minutes * this value = acquired sp IN GAME LIMITED TO INTEGER.MAX_VALUE SO THE MULTIPLY WITH REMAINING TIME CANT EXCEED IT (so field max value can't exceed 3579139.0 for 10hr) !! // Note real sp gain is exp gained / 250
    }

}
