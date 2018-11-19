package l2s.commons.versioning;

import java.io.File;
import java.io.IOException;
import java.util.jar.Attributes;
import java.util.jar.JarFile;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Version
{
    private static final Logger _log = LoggerFactory.getLogger(Version.class);

    private String _revisionNumber = "exported";
    private String _versionNumber = "-1";
    private String _buildDate = "";
    private String _buildJdk = "";
    private String _builderName = "";

    @SuppressWarnings("resource")
    public Version(Class<?> c)
    {
        File jarName = null;
        try
        {
            jarName = Locator.getClassSource(c);
            JarFile jarFile = new JarFile(jarName);

            Attributes attrs = jarFile.getManifest().getMainAttributes();

            setBuildJdk(attrs);

            setBuildDate(attrs);

            setRevisionNumber(attrs);

            setVersionNumber(attrs);

            setBuilderName(attrs);
        }
        catch(IOException e)
        {
            _log.error("Unable to get soft information\nFile name '" + (jarName == null ? "null" : jarName.getAbsolutePath()) + "' isn't a valid jar", e);
        }

    }

    /**
     * @param attrs
     */
    private void setBuilderName(Attributes attrs)
    {
        String builderName = attrs.getValue("Builder-Name");
        if(builderName != null)
            _builderName = builderName;
        else
        {
            builderName = attrs.getValue("Created-By");
            if(builderName != null)
                _builderName = builderName;
            else
                _builderName = "L2-Scripts";
        }
    }

    /**
     * @param attrs
     */
    private void setVersionNumber(Attributes attrs)
    {
        String versionNumber = attrs.getValue("Implementation-Version");
        if(versionNumber != null)
            _versionNumber = versionNumber;
        else
            _versionNumber = "-1";
    }

    /**
     * @param attrs
     */
    private void setRevisionNumber(Attributes attrs)
    {
        String revisionNumber = attrs.getValue("Implementation-Build");
        if(revisionNumber != null)
            _revisionNumber = revisionNumber;
        else
            _revisionNumber = "-1";
    }

    /**
     * @param attrs
     */
    private void setBuildJdk(Attributes attrs)
    {
        String buildJdk = attrs.getValue("Build-Jdk");
        if(buildJdk != null)
            _buildJdk = buildJdk;
        else
        {
            buildJdk = attrs.getValue("Created-By");
            if(buildJdk != null)
                _buildJdk = buildJdk;
            else
                _buildJdk = "-1";
        }
    }

    /**
     * @param attrs
     */
    private void setBuildDate(Attributes attrs)
    {
        String buildDate = attrs.getValue("Build-Date");
        if(buildDate != null)
            _buildDate = buildDate;
        else
            _buildDate = "-1";
    }

    public String getRevisionNumber()
    {
        return _revisionNumber;
    }

    public String getVersionNumber()
    {
        return _versionNumber;
    }

    public String getBuildDate()
    {
        return _buildDate;
    }

    public String getBuildJdk()
    {
        return _buildJdk;
    }

    public String getBuilderName()
    {
        return _builderName;
    }
}