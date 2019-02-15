module org.l2j.authserver {
    requires org.l2j.commons;

    requires java.xml.bind;
    requires java.sql;
    requires org.slf4j;
    requires spring.data.commons;
    requires spring.data.jdbc;
    requires io.github.joealisson.mmocore;

    exports org.l2j.authserver;
    opens org.l2j.authserver.xml to java.xml.bind;
    opens org.l2j.authserver.settings to org.l2j.commons;
}