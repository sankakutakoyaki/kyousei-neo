package com.kyouseipro.neo.domain.business.order.photo;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import java.nio.*;
import java.nio.file.*;
import java.awt.image.BufferedImage;
class PhotoOrientationTest {
    @TempDir Path temp;
    @Test void readsBothExifByteOrdersAndLeavesNonJpegAlone() throws Exception {
        for(var order:new ByteOrder[]{ByteOrder.LITTLE_ENDIAN,ByteOrder.BIG_ENDIAN}) {
            var bytes=ByteBuffer.allocate(36);bytes.putShort((short)0xffd8).putShort((short)0xffe1).putShort((short)32);
            bytes.put(new byte[]{'E','x','i','f',0,0});bytes.order(order);
            bytes.put(order==ByteOrder.LITTLE_ENDIAN?new byte[]{'I','I'}:new byte[]{'M','M'});
            bytes.putShort((short)42).putInt(8).putShort((short)1).putShort((short)0x112).putShort((short)3).putInt(1).putShort((short)6).putShort((short)0);
            Path file=temp.resolve(order.toString());Files.write(file,bytes.array());assertEquals(6,PhotoOrientation.read(file));
        }
        Path png=temp.resolve("image.png");Files.write(png,new byte[]{(byte)137,80,78,71});assertEquals(1,PhotoOrientation.read(png));
    }
    @Test void portraitRotationKeepsPixelPositions() {
        var source=new BufferedImage(3,2,BufferedImage.TYPE_INT_RGB);source.setRGB(0,0,0x112233);source.setRGB(2,1,0x445566);
        var rotated=PhotoOrientation.apply(source,6);assertEquals(2,rotated.getWidth());assertEquals(3,rotated.getHeight());
        assertEquals(source.getRGB(0,0),rotated.getRGB(1,0));assertEquals(source.getRGB(2,1),rotated.getRGB(0,2));
    }
}
