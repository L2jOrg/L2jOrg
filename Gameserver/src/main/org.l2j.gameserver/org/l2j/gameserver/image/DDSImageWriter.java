package org.l2j.gameserver.image;

import com.sun.imageio.plugins.png.PNGMetadata;

import javax.imageio.*;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.spi.ImageWriterSpi;
import java.awt.*;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.io.IOException;
import java.util.zip.Deflater;

public class DDSImageWriter extends ImageWriter {

    private int sourceXOffset;
    private int sourceYOffset;

    /**
     * Constructs an {@code ImageWriter} and sets its
     * {@code originatingProvider} instance variable to the
     * supplied value.
     *
     * <p> Subclasses that make use of extensions should provide a
     * constructor with signature {@code (ImageWriterSpi, Object)}
     * in order to retrieve the extension object.  If
     * the extension object is unsuitable, an
     * {@code IllegalArgumentException} should be thrown.
     *
     * @param originatingProvider the {@code ImageWriterSpi} that
     *                            is constructing this object, or {@code null}.
     */
    protected DDSImageWriter(ImageWriterSpi originatingProvider) {
        super(originatingProvider);
    }

    @Override
    public IIOMetadata getDefaultStreamMetadata(ImageWriteParam param) {
        return null;
    }

    @Override
    public IIOMetadata getDefaultImageMetadata(ImageTypeSpecifier imageType, ImageWriteParam param) {
        return null;
    }

    @Override
    public IIOMetadata convertStreamMetadata(IIOMetadata inData, ImageWriteParam param) {
        return null;
    }

    @Override
    public IIOMetadata convertImageMetadata(IIOMetadata inData, ImageTypeSpecifier imageType, ImageWriteParam param) {
        return null;
    }

    @Override
    public void write(IIOMetadata streamMetadata, IIOImage image, ImageWriteParam param) throws IOException {
        if (getOutput() == null) {
            throw new IllegalStateException("output == null!");
        }
        if (image == null) {
            throw new IllegalArgumentException("image == null!");
        }

        RenderedImage im = image.getRenderedImage();
        SampleModel sampleModel = im.getSampleModel();

        // Set source region and subsampling to default values
        this.sourceXOffset = im.getMinX();
        this.sourceYOffset = im.getMinY();
        this.sourceWidth = im.getWidth();
        this.sourceHeight = im.getHeight();
        this.sourceBands = null;
        this.periodX = 1;
        this.periodY = 1;

        if (param != null) {
            // Get source region and subsampling factors
            Rectangle sourceRegion = param.getSourceRegion();
            if (sourceRegion != null) {
                Rectangle imageBounds = new Rectangle(im.getMinX(),
                        im.getMinY(),
                        im.getWidth(),
                        im.getHeight());
                // Clip to actual image bounds
                sourceRegion = sourceRegion.intersection(imageBounds);
                sourceXOffset = sourceRegion.x;
                sourceYOffset = sourceRegion.y;
                sourceWidth = sourceRegion.width;
                sourceHeight = sourceRegion.height;
            }

            // Adjust for subsampling offsets
            int gridX = param.getSubsamplingXOffset();
            int gridY = param.getSubsamplingYOffset();
            sourceXOffset += gridX;
            sourceYOffset += gridY;
            sourceWidth -= gridX;
            sourceHeight -= gridY;

            // Get subsampling factors
            periodX = param.getSourceXSubsampling();
            periodY = param.getSourceYSubsampling();

            int[] sBands = param.getSourceBands();
            if (sBands != null) {
                sourceBands = sBands;
                numBands = sourceBands.length;
            }
        }

        // Compute output dimensions
        int destWidth = (sourceWidth + periodX - 1)/periodX;
        int destHeight = (sourceHeight + periodY - 1)/periodY;
        if (destWidth <= 0 || destHeight <= 0) {
            throw new IllegalArgumentException("Empty source region!");
        }

        // Compute total number of pixels for progress notification
        this.totalPixels = destWidth*destHeight;
        this.pixelsDone = 0;

        // Create metadata
        IIOMetadata imd = image.getMetadata();
        if (imd != null) {
            metadata = (PNGMetadata)convertImageMetadata(imd,
                    ImageTypeSpecifier.createFromRenderedImage(im),
                    null);
        } else {
            metadata = new PNGMetadata();
        }

        // reset compression level to default:
        int deflaterLevel = DEFAULT_COMPRESSION_LEVEL;

        if (param != null) {
            switch(param.getCompressionMode()) {
                case ImageWriteParam.MODE_DISABLED:
                    deflaterLevel = Deflater.NO_COMPRESSION;
                    break;
                case ImageWriteParam.MODE_EXPLICIT:
                    float quality = param.getCompressionQuality();
                    if (quality >= 0f && quality <= 1f) {
                        deflaterLevel = 9 - Math.round(9f * quality);
                    }
                    break;
                default:
            }

            // Use Adam7 interlacing if set in write param
            switch (param.getProgressiveMode()) {
                case ImageWriteParam.MODE_DEFAULT:
                    metadata.IHDR_interlaceMethod = 1;
                    break;
                case ImageWriteParam.MODE_DISABLED:
                    metadata.IHDR_interlaceMethod = 0;
                    break;
                // MODE_COPY_FROM_METADATA should already be taken care of
                // MODE_EXPLICIT is not allowed
                default:
            }
        }

        // Initialize bitDepth and colorType
        metadata.initialize(new ImageTypeSpecifier(im), numBands);

        // Overwrite IHDR width and height values with values from image
        metadata.IHDR_width = destWidth;
        metadata.IHDR_height = destHeight;

        this.bpp = numBands*((metadata.IHDR_bitDepth == 16) ? 2 : 1);

        // Initialize scaling tables for this image
        initializeScaleTables(sampleModel.getSampleSize());

        clearAbortRequest();

        processImageStarted(0);
        if (abortRequested()) {
            processWriteAborted();
        } else {
            try {
                write_magic();
                write_IHDR();

                write_cHRM();
                write_gAMA();
                write_iCCP();
                write_sBIT();
                write_sRGB();

                write_PLTE();

                write_hIST();
                write_tRNS();
                write_bKGD();

                write_pHYs();
                write_sPLT();
                write_tIME();
                write_tEXt();
                write_iTXt();
                write_zTXt();

                writeUnknownChunks();

                write_IDAT(im, deflaterLevel);

                if (abortRequested()) {
                    processWriteAborted();
                } else {
                    // Finish up and inform the listeners we are done
                    writeIEND();
                    processImageComplete();
                }
            } catch (IOException e) {
                throw new IIOException("I/O error writing PNG file!", e);
            }
        }
    }
}
