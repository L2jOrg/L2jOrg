package org.l2j.gameserver.communitybbs.Manager;

import org.l2j.gameserver.communitybbs.BB.Forum;
import org.l2j.gameserver.communitybbs.BB.Post;
import org.l2j.gameserver.communitybbs.BB.Topic;
import org.l2j.gameserver.handler.CommunityBoardHandler;
import org.l2j.gameserver.model.actor.instance.Player;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.concurrent.ConcurrentHashMap;

public class PostBBSManager extends BaseBBSManager {
    private final Map<Topic, Post> _postByTopic = new ConcurrentHashMap<>();

    private PostBBSManager() {
    }

    public Post getGPosttByTopic(Topic t) {
        Post post = _postByTopic.get(t);
        if (post == null) {
            post = new Post(t);
            _postByTopic.put(t, post);
        }
        return post;
    }

    public void delPostByTopic(Topic t) {
        _postByTopic.remove(t);
    }

    public void addPostByTopic(Post p, Topic t) {
        _postByTopic.putIfAbsent(t, p);
    }

    @Override
    public void parsecmd(String command, Player activeChar) {
        if (command.startsWith("_bbsposts;read;")) {
            final StringTokenizer st = new StringTokenizer(command, ";");
            st.nextToken();
            st.nextToken();
            final int idf = Integer.parseInt(st.nextToken());
            final int idp = Integer.parseInt(st.nextToken());
            final String index = st.hasMoreTokens() ? st.nextToken() : null;
            final int ind = index == null ? 1 : Integer.parseInt(index);
            showPost(TopicBBSManager.getInstance().getTopicByID(idp), ForumsBBSManager.getInstance().getForumByID(idf), activeChar, ind);
        } else if (command.startsWith("_bbsposts;edit;")) {
            final StringTokenizer st = new StringTokenizer(command, ";");
            st.nextToken();
            st.nextToken();
            final int idf = Integer.parseInt(st.nextToken());
            final int idt = Integer.parseInt(st.nextToken());
            final int idp = Integer.parseInt(st.nextToken());
            showEditPost(TopicBBSManager.getInstance().getTopicByID(idt), ForumsBBSManager.getInstance().getForumByID(idf), activeChar, idp);
        } else {
            CommunityBoardHandler.separateAndSend("<html><body><br><br><center>the command: " + command + " is not implemented yet</center><br><br></body></html>", activeChar);
        }
    }

    private void showEditPost(Topic topic, Forum forum, Player activeChar, int idp) {
        final Post p = getGPosttByTopic(topic);
        if ((forum == null) || (topic == null) || (p == null)) {
            CommunityBoardHandler.separateAndSend("<html><body><br><br><center>Error, this forum, topic or post does not exist!</center><br><br></body></html>", activeChar);
        } else {
            showHtmlEditPost(topic, activeChar, forum, p);
        }
    }

    private void showPost(Topic topic, Forum forum, Player activeChar, int ind) {
        if ((forum == null) || (topic == null)) {
            CommunityBoardHandler.separateAndSend("<html><body><br><br><center>Error: This forum is not implemented yet!</center></body></html>", activeChar);
        } else if (forum.getType() == Forum.MEMO) {
            showMemoPost(topic, activeChar, forum);
        } else {
            CommunityBoardHandler.separateAndSend("<html><body><br><br><center>The forum: " + forum.getName() + " is not implemented yet!</center></body></html>", activeChar);
        }
    }

    private void showHtmlEditPost(Topic topic, Player activeChar, Forum forum, Post p) {
        final String html = "<html><body><br><br><table border=0 width=610><tr><td width=10></td><td width=600 align=left><a action=\"bypass _bbshome\">HOME</a>&nbsp;>&nbsp;<a action=\"bypass _bbsmemo\">" + forum.getName() + " Form</a></td></tr></table><img src=\"L2UI.squareblank\" width=\"1\" height=\"10\"><center><table border=0 cellspacing=0 cellpadding=0><tr><td width=610><img src=\"sek.cbui355\" width=\"610\" height=\"1\"><br1><img src=\"sek.cbui355\" width=\"610\" height=\"1\"></td></tr></table><table fixwidth=610 border=0 cellspacing=0 cellpadding=0><tr><td><img src=\"l2ui.mini_logo\" width=5 height=20></td></tr><tr><td><img src=\"l2ui.mini_logo\" width=5 height=1></td><td align=center FIXWIDTH=60 height=29>&$413;</td><td FIXWIDTH=540>" + topic.getName() + "</td><td><img src=\"l2ui.mini_logo\" width=5 height=1></td></tr></table><table fixwidth=610 border=0 cellspacing=0 cellpadding=0><tr><td><img src=\"l2ui.mini_logo\" width=5 height=10></td></tr><tr><td><img src=\"l2ui.mini_logo\" width=5 height=1></td><td align=center FIXWIDTH=60 height=29 valign=top>&$427;</td><td align=center FIXWIDTH=540><MultiEdit var =\"Content\" width=535 height=313></td><td><img src=\"l2ui.mini_logo\" width=5 height=1></td></tr><tr><td><img src=\"l2ui.mini_logo\" width=5 height=10></td></tr></table><table fixwidth=610 border=0 cellspacing=0 cellpadding=0><tr><td><img src=\"l2ui.mini_logo\" width=5 height=10></td></tr><tr><td><img src=\"l2ui.mini_logo\" width=5 height=1></td><td align=center FIXWIDTH=60 height=29>&nbsp;</td><td align=center FIXWIDTH=70><button value=\"&$140;\" action=\"Write Post " + forum.getID() + ";" + topic.getID() + ";0 _ Content Content Content\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\" ></td><td align=center FIXWIDTH=70><button value = \"&$141;\" action=\"bypass _bbsmemo\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\"> </td><td align=center FIXWIDTH=400>&nbsp;</td><td><img src=\"l2ui.mini_logo\" width=5 height=1></td></tr></table></center></body></html>";
        send1001(html, activeChar);
        send1002(activeChar, p.getCPost(0).postTxt, topic.getName(), DateFormat.getInstance().format(new Date(topic.getDate())));
    }

    private void showMemoPost(Topic topic, Player activeChar, Forum forum) {
        final Post p = getGPosttByTopic(topic);
        final Locale locale = Locale.getDefault();
        final DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.FULL, locale);

        String mes = p.getCPost(0).postTxt.replace(">", "&gt;");
        mes = mes.replace("<", "&lt;");

        final String html = "<html><body><br><br><table border=0 width=610><tr><td width=10></td><td width=600 align=left><a action=\"bypass _bbshome\">HOME</a>&nbsp;>&nbsp;<a action=\"bypass _bbsmemo\">Memo Form</a></td></tr></table><img src=\"L2UI.squareblank\" width=\"1\" height=\"10\"><center><table border=0 cellspacing=0 cellpadding=0 bgcolor=333333><tr><td height=10></td></tr><tr><td fixWIDTH=55 align=right valign=top>&$413; : &nbsp;</td><td fixWIDTH=380 valign=top>" + topic.getName() + "</td><td fixwidth=5></td><td fixwidth=50></td><td fixWIDTH=120></td></tr><tr><td height=10></td></tr><tr><td align=right><font color=\"AAAAAA\" >&$417; : &nbsp;</font></td><td><font color=\"AAAAAA\">" + topic.getOwnerName() + "</font></td><td></td><td><font color=\"AAAAAA\">&$418; :</font></td><td><font color=\"AAAAAA\">" + dateFormat.format(p.getCPost(0).postDate) + "</font></td></tr><tr><td height=10></td></tr></table><br><table border=0 cellspacing=0 cellpadding=0><tr><td fixwidth=5></td><td FIXWIDTH=600 align=left>" + mes + "</td><td fixqqwidth=5></td></tr></table><br><img src=\"L2UI.squareblank\" width=\"1\" height=\"5\"><img src=\"L2UI.squaregray\" width=\"610\" height=\"1\"><img src=\"L2UI.squareblank\" width=\"1\" height=\"5\"><table border=0 cellspacing=0 cellpadding=0 FIXWIDTH=610><tr><td width=50><button value=\"&$422;\" action=\"bypass _bbsmemo\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\"></td><td width=560 align=right><table border=0 cellspacing=0><tr><td FIXWIDTH=300></td><td><button value = \"&$424;\" action=\"bypass _bbsposts;edit;" + forum.getID() + ";" + topic.getID() + ";0\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\" ></td>&nbsp;<td><button value = \"&$425;\" action=\"bypass _bbstopics;del;" + forum.getID() + ";" + topic.getID() + "\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\" ></td>&nbsp;<td><button value = \"&$421;\" action=\"bypass _bbstopics;crea;" + forum.getID() + "\" back=\"l2ui_ch3.smallbutton2_down\" width=65 height=20 fore=\"l2ui_ch3.smallbutton2\" ></td>&nbsp;</tr></table></td></tr></table><br><br><br></center></body></html>";
        CommunityBoardHandler.separateAndSend(html, activeChar);
    }

    @Override
    public void parsewrite(String ar1, String ar2, String ar3, String ar4, String ar5, Player activeChar) {
        final StringTokenizer st = new StringTokenizer(ar1, ";");
        final int idf = Integer.parseInt(st.nextToken());
        final int idt = Integer.parseInt(st.nextToken());
        final int idp = Integer.parseInt(st.nextToken());

        final Forum f = ForumsBBSManager.getInstance().getForumByID(idf);
        if (f == null) {
            CommunityBoardHandler.separateAndSend("<html><body><br><br><center>the forum: " + idf + " does not exist !</center><br><br></body></html>", activeChar);
        } else {
            final Topic t = f.getTopic(idt);
            if (t == null) {
                CommunityBoardHandler.separateAndSend("<html><body><br><br><center>the topic: " + idt + " does not exist !</center><br><br></body></html>", activeChar);
            } else {
                final Post p = getGPosttByTopic(t);
                if (p != null) {
                    if (p.getCPost(idp) == null) {
                        CommunityBoardHandler.separateAndSend("<html><body><br><br><center>the post: " + idp + " does not exist !</center><br><br></body></html>", activeChar);
                    } else {
                        p.getCPost(idp).postTxt = ar4;
                        p.updatetxt(idp);
                        parsecmd("_bbsposts;read;" + f.getID() + ";" + t.getID(), activeChar);
                    }
                }
            }
        }
    }

    public static PostBBSManager getInstance() {
        return Singleton.INSTANCE;
    }

    private static class Singleton {
        private static final PostBBSManager INSTANCE = new PostBBSManager();
    }
}
