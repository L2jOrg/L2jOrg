package org.l2j.gameserver.image;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriter;
import javax.imageio.spi.ImageWriterSpi;
import javax.imageio.stream.ImageOutputStream;
import java.awt.image.BufferedImage;
import java.util.Locale;

public class SimpleDDSDxT1ImageWriterSpi extends ImageWriterSpi {

    private static final ImageTypeSpecifier handledType = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_USHORT_565_RGB);

    private static final String vendorName = "L2jOrg";

    private static final String version = "1.0";

    private static final String[] names = { "dds", "DDS" };

    private static final String[] suffixes = { "dds" };

    private static final String[] MIMETypes = { "image/dds" };

    private static final String writerClassName = "org.l2j.gameserver.image.DDSImageWriter";

    private static final String[] readerSpiNames = {
            "org.l2j.gameserver.image.SimpleDDSDxT1ImageWriterSpi"
    };

    public SimpleDDSDxT1ImageWriterSpi() {
        super(vendorName,
                version,
                names,
                suffixes,
                MIMETypes,
                writerClassName,
                new Class<?>[] { ImageOutputStream.class },
                readerSpiNames,
                false,
                null, null,
                null, null,
                true,
                DDSMetadata.nativeMetadataFormatName,
                "org.l2j.gameserver.image.DDSMetadataFormat",
                null, null);

    }

    @Override
    public boolean canEncodeImage(ImageTypeSpecifier type) {
        return handledType.equals(type);
    }

    @Override
    public ImageWriter createWriterInstance(Object extension) {
        return new DDSImageWriter(this);
    }

    @Override
    public String getDescription(Locale locale) {
        return "Standard DDS image writer (DXT1 Support only)";
    }
}
