package org.l2j.gameserver.communitybbs.BB;

import org.l2j.commons.database.DatabaseFactory;
import org.l2j.gameserver.communitybbs.Manager.PostBBSManager;
import org.l2j.gameserver.data.database.data.PostData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author Maktakien
 */
public class Post {
    private static final Logger LOGGER = LoggerFactory.getLogger(Post.class);
    private final Collection<PostData> posts;

    public Post(String _PostOwner, int _PostOwnerID, long date, int tid, int _PostForumID, String txt) {
        posts = ConcurrentHashMap.newKeySet();
        final PostData data = new PostData();
        data.postId = 0;
        data.postOwner = _PostOwner;
        data.postOwnerId = _PostOwnerID;
        data.postDate = date;
        data.postTopicId = tid;
        data.postForumId = _PostForumID;
        data.postTxt = txt;
        posts.add(data);
        insertindb(data);
    }

    public Post(Topic t) {
        posts = ConcurrentHashMap.newKeySet();
        load(t);
    }

    private void insertindb(PostData cp) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("INSERT INTO posts (post_id,post_owner_name,post_ownerid,post_date,post_topic_id,post_forum_id,post_txt) values (?,?,?,?,?,?,?)")) {
            ps.setInt(1, cp.postId);
            ps.setString(2, cp.postOwner);
            ps.setInt(3, cp.postOwnerId);
            ps.setLong(4, cp.postDate);
            ps.setInt(5, cp.postTopicId);
            ps.setInt(6, cp.postForumId);
            ps.setString(7, cp.postTxt);
            ps.execute();
        } catch (Exception e) {
            LOGGER.warn("Error while saving new Post to db " + e.getMessage(), e);
        }
    }

    public PostData getCPost(int id) {
        int i = 0;
        for (PostData cp : posts) {
            if (i++ == id) {
                return cp;
            }
        }
        return null;
    }

    public void deleteme(Topic t) {
        PostBBSManager.getInstance().delPostByTopic(t);
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("DELETE FROM posts WHERE post_forum_id=? AND post_topic_id=?")) {
            ps.setInt(1, t.getForumID());
            ps.setInt(2, t.getID());
            ps.execute();
        } catch (Exception e) {
            LOGGER.warn("Error while deleting post: " + e.getMessage(), e);
        }
    }

    private void load(Topic t) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("SELECT * FROM posts WHERE post_forum_id=? AND post_topic_id=? ORDER BY post_id ASC")) {
            ps.setInt(1, t.getForumID());
            ps.setInt(2, t.getID());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    final PostData cp = new PostData();
                    cp.postId = rs.getInt("post_id");
                    cp.postOwner = rs.getString("post_owner_name");
                    cp.postOwnerId = rs.getInt("post_ownerid");
                    cp.postDate = rs.getLong("post_date");
                    cp.postTopicId = rs.getInt("post_topic_id");
                    cp.postForumId = rs.getInt("post_forum_id");
                    cp.postTxt = rs.getString("post_txt");
                    posts.add(cp);
                }
            }
        } catch (Exception e) {
            LOGGER.warn("Data error on Post " + t.getForumID() + "/" + t.getID() + " : " + e.getMessage(), e);
        }
    }

    /**
     * @param i
     */
    public void updatetxt(int i) {
        try (Connection con = DatabaseFactory.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement("UPDATE posts SET post_txt=? WHERE post_id=? AND post_topic_id=? AND post_forum_id=?")) {
            final PostData cp = getCPost(i);
            ps.setString(1, cp.postTxt);
            ps.setInt(2, cp.postId);
            ps.setInt(3, cp.postTopicId);
            ps.setInt(4, cp.postForumId);
            ps.execute();
        } catch (Exception e) {
            LOGGER.warn("Error while saving new Post to db " + e.getMessage(), e);
        }
    }

}
