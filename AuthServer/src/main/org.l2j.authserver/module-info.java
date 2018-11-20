module org.l2j.authserver {
    requires org.l2j.commons;
    requires org.l2j.mmocore;

    requires java.xml.bind;
    requires java.sql;
    requires org.slf4j;
    requires spring.data.commons;

    exports org.l2j.authserver;
    opens org.l2j.authserver.xml to java.xml.bind;
}