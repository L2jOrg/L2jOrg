package org.l2j.gameserver.model.announce;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.Config;
import org.l2j.commons.threading.ThreadPoolManager;
import org.l2j.gameserver.util.Broadcast;

import java.sql.*;
import java.util.concurrent.ScheduledFuture;

/**
 * @author UnAfraid
 */
public final class AutoAnnouncement extends Announcement implements Runnable {
    private static final String INSERT_QUERY = "INSERT INTO announcements (`type`, `content`, `author`, `initial`, `delay`, `repeat`) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String UPDATE_QUERY = "UPDATE announcements SET `type` = ?, `content` = ?, `author` = ?, `initial` = ?, `delay` = ?, `repeat` = ? WHERE id = ?";

    private long _initial;
    private long _delay;
    private int _repeat = -1;
    private int _currentState;
    private ScheduledFuture<?> _task;

    public AutoAnnouncement(AnnouncementType type, String content, String author, long initial, long delay, int repeat) {
        super(type, content, author);
        _initial = initial;
        _delay = delay;
        _repeat = repeat;
        restartMe();
    }

    public AutoAnnouncement(ResultSet rset) throws SQLException {
        super(rset);
        _initial = rset.getLong("initial");
        _delay = rset.getLong("delay");
        _repeat = rset.getInt("repeat");
        restartMe();
    }

    public long getInitial() {
        return _initial;
    }

    public void setInitial(long initial) {
        _initial = initial;
    }

    public long getDelay() {
        return _delay;
    }

    public void setDelay(long delay) {
        _delay = delay;
    }

    public int getRepeat() {
        return _repeat;
    }

    public void setRepeat(int repeat) {
        _repeat = repeat;
    }

    @Override
    public boolean storeMe() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement st = con.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS)) {
            st.setInt(1, getType().ordinal());
            st.setString(2, getContent());
            st.setString(3, getAuthor());
            st.setLong(4, _initial);
            st.setLong(5, _delay);
            st.setInt(6, _repeat);
            st.execute();
            try (ResultSet rset = st.getGeneratedKeys()) {
                if (rset.next()) {
                    _id = rset.getInt(1);
                }
            }
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Couldn't store announcement: ", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean updateMe() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement st = con.prepareStatement(UPDATE_QUERY)) {
            st.setInt(1, getType().ordinal());
            st.setString(2, getContent());
            st.setString(3, getAuthor());
            st.setLong(4, _initial);
            st.setLong(5, _delay);
            st.setLong(6, _repeat);
            st.setLong(7, getId());
            st.execute();
        } catch (Exception e) {
            LOGGER.warn(getClass().getSimpleName() + ": Couldn't update announcement: ", e);
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteMe() {
        if ((_task != null) && !_task.isCancelled()) {
            _task.cancel(false);
        }
        return super.deleteMe();
    }

    public void restartMe() {
        if ((_task != null) && !_task.isCancelled()) {
            _task.cancel(false);
        }
        _currentState = _repeat;
        _task = ThreadPoolManager.getInstance().schedule(this, _initial);
    }

    @Override
    public void run() {
        if ((_currentState == -1) || (_currentState > 0)) {
            for (String content : getContent().split(Config.EOL)) {
                Broadcast.toAllOnlinePlayers(content, (getType() == AnnouncementType.AUTO_CRITICAL));
            }

            if (_currentState != -1) {
                _currentState--;
            }

            _task = ThreadPoolManager.getInstance().schedule(this, _delay);
        }
    }
}
