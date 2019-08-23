package org.l2j.gameserver.communitybbs.Manager;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.communitybbs.BB.Forum;
import org.l2j.gameserver.model.actor.instance.Player;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

public class ForumsBBSManager extends BaseBBSManager {
    private static final Logger LOGGER = LoggerFactory.getLogger(ForumsBBSManager.class);
    private final Collection<Forum> _table;
    private int _lastid = 1;

    private ForumsBBSManager() {
        _table = ConcurrentHashMap.newKeySet();
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             Statement s = con.createStatement();
             ResultSet rs = s.executeQuery("SELECT forum_id FROM forums WHERE forum_type = 0")) {
            while (rs.next()) {
                addForum(new Forum(rs.getInt("forum_id"), null));
            }
        } catch (Exception e) {
            LOGGER.warn("Data error on Forum (root): " + e.getMessage(), e);
        }
    }

    /**
     * Inits the root.
     */
    public void initRoot() {
        _table.forEach(Forum::vload);
        LOGGER.info(getClass().getSimpleName() + ": Loaded " + _table.size() + " forums. Last forum id used: " + _lastid);
    }

    /**
     * Adds the forum.
     *
     * @param ff the forum
     */
    public void addForum(Forum ff) {
        if (ff == null) {
            return;
        }

        _table.add(ff);

        if (ff.getID() > _lastid) {
            _lastid = ff.getID();
        }
    }

    @Override
    public void parsecmd(String command, Player activeChar) {
    }

    /**
     * Gets the forum by name.
     *
     * @param name the forum name
     * @return the forum by name
     */
    public Forum getForumByName(String name) {
        for (Forum f : _table) {
            if (f.getName().equals(name)) {
                return f;
            }
        }
        return null;
    }

    /**
     * Creates the new forum.
     *
     * @param name   the forum name
     * @param parent the parent forum
     * @param type   the forum type
     * @param perm   the perm
     * @param oid    the oid
     * @return the new forum
     */
    public Forum createNewForum(String name, Forum parent, int type, int perm, int oid) {
        final Forum forum = new Forum(name, parent, type, perm, oid);
        forum.insertIntoDb();
        return forum;
    }

    /**
     * Gets the a new Id.
     *
     * @return the a new Id
     */
    public int getANewID() {
        return ++_lastid;
    }

    /**
     * Gets the forum by Id.
     *
     * @param idf the the forum Id
     * @return the forum by Id
     */
    public Forum getForumByID(int idf) {
        for (Forum f : _table) {
            if (f.getID() == idf) {
                return f;
            }
        }
        return null;
    }

    @Override
    public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, Player activeChar) {
    }

    public static ForumsBBSManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final ForumsBBSManager INSTANCE = new ForumsBBSManager();
    }
}