package com.kyouseipro.neo.domain.business.order.photo;

import java.io.*;
import java.nio.*;
import java.nio.file.*;
import java.awt.image.BufferedImage;

/** スマホJPEGの回転情報を縮小画像にも反映し、原本は変更しない。 */
final class PhotoOrientation {
    private PhotoOrientation() {}
    static int read(Path path) throws IOException {
        try(var in=new DataInputStream(Files.newInputStream(path))) {
            if(in.readUnsignedShort()!=0xffd8)return 1;
            for(int segment=0;segment<100;segment++) {
                if(in.readUnsignedByte()!=0xff)return 1;
                int marker=in.readUnsignedByte();while(marker==0xff)marker=in.readUnsignedByte();
                if(marker==0xda || marker==0xd9)return 1;
                int length=in.readUnsignedShort()-2;if(length<0)return 1;
                byte[] bytes=in.readNBytes(length);if(bytes.length!=length)return 1;
                if(marker!=0xe1 || length<14 || bytes[0]!='E' || bytes[1]!='x' || bytes[2]!='i' || bytes[3]!='f' || bytes[4]!=0 || bytes[5]!=0)continue;
                var data=ByteBuffer.wrap(bytes).order(bytes[6]=='I' && bytes[7]=='I'?ByteOrder.LITTLE_ENDIAN:ByteOrder.BIG_ENDIAN);
                if(data.getShort(8)!=42)return 1;
                long offset=6+Integer.toUnsignedLong(data.getInt(10));if(offset>length-2)return 1;
                int start=(int)offset,count=Short.toUnsignedInt(data.getShort(start));
                for(int i=0;i<count;i++) {
                    long entry=(long)start+2+12L*i;if(entry>length-12)return 1;
                    int at=(int)entry;
                    if(Short.toUnsignedInt(data.getShort(at))==0x112 && data.getShort(at+2)==3 && data.getInt(at+4)==1) {
                        int value=Short.toUnsignedInt(data.getShort(at+8));return value>=1 && value<=8?value:1;
                    }
                }
                return 1;
            }
        }catch(EOFException ignored){}
        return 1;
    }
    static BufferedImage apply(BufferedImage image,int orientation) {
        if(orientation<=1 || orientation>8)return image;
        int width=image.getWidth(),height=image.getHeight();
        var result=new BufferedImage(orientation>=5?height:width,orientation>=5?width:height,BufferedImage.TYPE_INT_RGB);
        for(int y=0;y<height;y++)for(int x=0;x<width;x++) {
            int dx=switch(orientation){case 2,3->width-1-x;case 5,8->y;case 6,7->height-1-y;default->x;};
            int dy=switch(orientation){case 3,4->height-1-y;case 5,6->x;case 7,8->width-1-x;default->y;};
            result.setRGB(dx,dy,image.getRGB(x,y));
        }
        return result;
    }
}
