package org.l2j.gameserver.image;

import javax.imageio.ImageTypeSpecifier;
import javax.imageio.metadata.IIOMetadataFormatImpl;

public class DDSMetadataFormat extends IIOMetadataFormatImpl {

    public DDSMetadataFormat(String rootName, int childPolicy) {
        super(rootName, childPolicy);
    }

    @Override
    public boolean canNodeAppear(String elementName, ImageTypeSpecifier imageType) {
        return false;
    }
}
