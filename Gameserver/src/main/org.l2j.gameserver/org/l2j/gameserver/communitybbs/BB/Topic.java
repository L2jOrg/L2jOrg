/*
 * This file is part of the L2J Mobius project.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package org.l2j.gameserver.communitybbs.BB;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.communitybbs.Manager.TopicBBSManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Topic {
    public static final int MORMAL = 0;
    public static final int MEMO = 1;
    private static final Logger LOGGER = Logger.getLogger(Topic.class.getName());
    private final int _id;
    private final int _forumId;
    private final String _topicName;
    private final long _date;
    private final String _ownerName;
    private final int _ownerId;
    private final int _type;
    private final int _cReply;

    /**
     * @param ct
     * @param id
     * @param fid
     * @param name
     * @param date
     * @param oname
     * @param oid
     * @param type
     * @param Creply
     */
    public Topic(ConstructorType ct, int id, int fid, String name, long date, String oname, int oid, int type, int Creply) {
        _id = id;
        _forumId = fid;
        _topicName = name;
        _date = date;
        _ownerName = oname;
        _ownerId = oid;
        _type = type;
        _cReply = Creply;
        TopicBBSManager.getInstance().addTopic(this);

        if (ct == ConstructorType.CREATE) {
            insertindb();
        }
    }

    private void insertindb() {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO topic (topic_id,topic_forum_id,topic_name,topic_date,topic_ownername,topic_ownerid,topic_type,topic_reply) values (?,?,?,?,?,?,?,?)")) {
            ps.setInt(1, _id);
            ps.setInt(2, _forumId);
            ps.setString(3, _topicName);
            ps.setLong(4, _date);
            ps.setString(5, _ownerName);
            ps.setInt(6, _ownerId);
            ps.setInt(7, _type);
            ps.setInt(8, _cReply);
            ps.execute();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error while saving new Topic to db " + e.getMessage(), e);
        }
    }

    /**
     * @return the topic Id
     */
    public int getID() {
        return _id;
    }

    public int getForumID() {
        return _forumId;
    }

    /**
     * @return the topic name
     */
    public String getName() {
        return _topicName;
    }

    public String getOwnerName() {
        return _ownerName;
    }

    /**
     * @param f
     */
    public void deleteme(Forum f) {
        TopicBBSManager.getInstance().delTopic(this);
        f.rmTopicByID(_id);
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM topic WHERE topic_id=? AND topic_forum_id=?")) {
            ps.setInt(1, _id);
            ps.setInt(2, f.getID());
            ps.execute();
        } catch (Exception e) {
            LOGGER.log(Level.WARNING, "Error while deleting topic: " + e.getMessage(), e);
        }
    }

    /**
     * @return the topic date
     */
    public long getDate() {
        return _date;
    }

    public enum ConstructorType {
        RESTORE,
        CREATE
    }
}
