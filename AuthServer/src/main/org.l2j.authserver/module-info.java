module org.l2j.authserver {
    requires org.l2j.commons;

    requires java.xml.bind;
    requires java.sql;
    requires org.slf4j;
    requires spring.data.commons;
    requires async.mmocore;
    requires spring.data.jdbc;

    exports org.l2j.authserver;
    opens org.l2j.authserver.xml to java.xml.bind;
}