package org.l2j.gameserver.image;

import org.w3c.dom.Node;

import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;

public class DDSMetadata extends IIOMetadata {

    public static String nativeMetadataFormatName = "org_l2j_dds_1.0";

    @Override
    public boolean isReadOnly() {
        return false;
    }

    @Override
    public Node getAsTree(String formatName) {
        return null;
    }

    @Override
    public void mergeTree(String formatName, Node root) throws IIOInvalidTreeException {

    }

    @Override
    public void reset() {

    }
}
