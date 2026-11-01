package com.kyouseipro.neo.domain.business.order.photo;

import java.util.*;
import java.time.LocalDate;
import java.io.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.*;
import com.kyouseipro.neo.domain.attachment.application.*;
import com.kyouseipro.neo.common.response.SimpleResponse;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order-photos")
public class OrderPhotoController {
    private final OrderPhotoService photos;
    private final AttachmentService attachments;
    private final AttachmentAccessService access;
    @GetMapping
    public List<Map<String,Object>> list(Authentication auth,@RequestParam(required=false) Long orderId,
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate from,
        @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate to,
        @RequestParam(required=false) String category,@RequestParam(defaultValue="0") int offset) {
        return photos.list(auth,orderId,from,to,category==null || category.isBlank()?null:category,offset);
    }
    @PostMapping(value="/{orderId}",consumes=MediaType.MULTIPART_FORM_DATA_VALUE)
    public SimpleResponse<List<Long>> upload(Authentication auth,@PathVariable long orderId,@RequestParam String category,
        @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate workDate,@RequestParam UUID requestId,@RequestParam("files") List<MultipartFile> files) {
        return SimpleResponse.ok("写真を保存しました。",photos.upload(auth,orderId,category,workDate,files,requestId));
    }
    @GetMapping(value="/preview/{id}",produces=MediaType.IMAGE_JPEG_VALUE)
    public ResponseEntity<byte[]> preview(Authentication auth,@PathVariable long id) throws IOException {
        access.file(auth,id,false);
        var file=attachments.findFile(id);
        try(var stream=ImageIO.createImageInputStream(file.path().toFile())) {
            var readers=ImageIO.getImageReaders(stream);
            if(!readers.hasNext())return ResponseEntity.notFound().build();
            var reader=readers.next();
            try {
                reader.setInput(stream);
                int width=reader.getWidth(0),height=reader.getHeight(0);
                var param=reader.getDefaultReadParam();int sample=Math.max(1,Math.max(width,height)/640);
                param.setSourceSubsampling(sample,sample,0,0);
                BufferedImage image=reader.read(0,param);
                double ratio=Math.min(1d,640d/Math.max(image.getWidth(),image.getHeight()));
                var thumb=new BufferedImage(Math.max(1,(int)(image.getWidth()*ratio)),Math.max(1,(int)(image.getHeight()*ratio)),BufferedImage.TYPE_INT_RGB);
                var graphics=thumb.createGraphics();try{graphics.setColor(java.awt.Color.WHITE);graphics.fillRect(0,0,thumb.getWidth(),thumb.getHeight());graphics.drawImage(image,0,0,thumb.getWidth(),thumb.getHeight(),null);}finally{graphics.dispose();}
                var out=new ByteArrayOutputStream();ImageIO.write(PhotoOrientation.apply(thumb,PhotoOrientation.read(file.path())),"jpg",out);
                return ResponseEntity.ok().cacheControl(CacheControl.noStore()).body(out.toByteArray());
            } finally {reader.dispose();}
        }
    }
}
