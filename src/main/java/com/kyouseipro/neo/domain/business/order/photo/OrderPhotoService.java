package com.kyouseipro.neo.domain.business.order.photo;

import java.util.*;
import com.kyouseipro.neo.common.enums.code.*;
import com.kyouseipro.neo.common.enums.util.EnumUtil;
import java.time.LocalDate;
import java.nio.file.*;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.*;
import org.springframework.web.multipart.MultipartFile;
import com.kyouseipro.neo.security.*;
import com.kyouseipro.neo.domain.attachment.application.AttachmentService;
import com.kyouseipro.neo.domain.corporation.api.office.OwnOfficeContextService;
import com.kyouseipro.neo.sql.repository.SqlRepository;
import com.kyouseipro.neo.sql.query.business.order.OrderQuery;
import com.kyouseipro.neo.common.exception.BusinessException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderPhotoService {
    private final AttachmentService attachments;
    private final SqlRepository sql;
    private final OrderScopeGuard guard;
    private final OwnOfficeContextService context;

    @Transactional
    public List<Long> upload(Authentication auth,long orderId,String category,LocalDate day,List<MultipartFile> files) {
        return upload(auth,orderId,category,day,files,UUID.randomUUID());
    }
    @Transactional
    public List<Long> upload(Authentication auth,long orderId,String category,LocalDate day,List<MultipartFile> files,UUID requestId) {
        guard.require(auth,orderId,false);
        var photoCategory=EnumUtil.byName(PhotoCategory.class,category);
        if(photoCategory==null || day==null)throw new BusinessException("写真分類と対象訪問日を選択してください。");
        if(files==null || files.isEmpty() || files.size()>20)throw new BusinessException("写真は1回に20枚まで選択してください。");
        for(var file:files)if(file==null || file.isEmpty() || file.getSize()>20L*1024*1024 || !Set.of("image/jpeg","image/png","image/gif").contains(Objects.toString(file.getContentType(),"")))
            throw new BusinessException("写真は1枚20MB以内のJPEG・PNG・GIFを選択してください。");
        if(files.stream().mapToLong(MultipartFile::getSize).sum()>90L*1024*1024)throw new BusinessException("写真は合計90MB以内で選択してください。");
        String editor=context.getAccount(auth);
        if(editor==null || editor.isBlank())throw new BusinessException("ログイン情報を確認してください。");
        sql.selectMap("SELECT order_id FROM orders WITH (UPDLOCK,HOLDLOCK) WHERE order_id=? AND state=0",List.of(orderId));
        if(requestId==null)throw new BusinessException("写真を選び直してください。");
        var duplicate=sql.selectMap("SELECT a.attachment_id,a.photo_category,a.work_date,a.uploaded_by,a.original_name,a.file_size,g.parent_id FROM attachments a JOIN attachment_groups g ON g.attachment_group_id=a.attachment_group_id WHERE a.upload_request_id=? ORDER BY a.upload_index",List.of(requestId.toString()));
        if(!duplicate.isEmpty()) {
            if(duplicate.size()!=files.size())throw new BusinessException("写真の送信要求が重複しています。");
            for(int i=0;i<duplicate.size();i++) {
                var old=duplicate.get(i);var file=files.get(i);
                if(((Number)old.get("parentId")).longValue()!=orderId || !Objects.equals(old.get("photoCategory"),category)
                    || !day.toString().equals(Objects.toString(old.get("workDate"),"")) || !editor.equals(old.get("uploadedBy"))
                    || ((Number)old.get("fileSize")).longValue()!=file.getSize() || !Objects.equals(old.get("originalName"),Path.of(Objects.toString(file.getOriginalFilename(),"file")).getFileName().toString().trim()))
                    throw new BusinessException("写真の送信要求が重複しています。");
            }
            return duplicate.stream().map(row->((Number)row.get("attachmentId")).longValue()).toList();
        }
        String folder=photoCategory.getFolderName()+"（"+day+"）";
        long group=attachments.findGroups("ORDER",orderId).stream().filter(g->folder.equals(g.groupName())).mapToLong(g->g.attachmentGroupId()).findFirst().orElseGet(()->attachments.createGroup("ORDER",orderId,folder));
        var ids=new ArrayList<Long>();
        int index=0;
        for(var file:files) {
            long id=attachments.upload("ORDER",orderId,group,List.of(file)).get(0);
            Path path=attachments.findFile(id).path();
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization(){
                @Override public void afterCompletion(int status){if(status!=STATUS_COMMITTED)try{Files.deleteIfExists(path);}catch(java.io.IOException ignored){}}
            });
            sql.updateRequired("UPDATE attachments SET photo_category=?,work_date=?,uploaded_by=?,upload_request_id=?,upload_index=? WHERE attachment_id=?",List.of(category,java.sql.Date.valueOf(day),editor,requestId.toString(),index++,id),"写真情報を保存できませんでした。");
            ids.add(id);
        }
        return ids;
    }

    public List<Map<String,Object>> list(Authentication auth,Long orderId,LocalDate from,LocalDate to,String category,int offset) {
        var scope=guard.readCondition(auth);
        if(orderId!=null)guard.require(auth,orderId,false);
        if(offset<0 || offset>100000)throw new BusinessException("一覧を再検索してください。");
        if(category!=null && !category.isBlank() && EnumUtil.byName(PhotoCategory.class,category)==null)throw new BusinessException("写真分類を確認してください。");
        var params=new ArrayList<Object>(Arrays.asList(orderId,orderId,from,from,to,to,category,category));
        params.addAll(Arrays.asList(scope.scope(),scope.scope(),scope.officeId(),scope.scope(),scope.employeeId()));params.add(offset);
        return sql.selectMap("""
            SELECT a.attachment_id,a.display_name,a.photo_category,CONVERT(char(10),a.work_date,23) AS work_date,a.uploaded_by,CONVERT(char(19),a.regist_date,120) AS regist_date,
                o.order_id,o.title,o.full_address,g.group_name
            FROM attachments a
            JOIN attachment_groups g ON g.attachment_group_id=a.attachment_group_id AND g.state=0 AND g.parent_type='ORDER'
            JOIN orders o ON o.order_id=g.parent_id AND o.state=0
            WHERE a.state=0 AND a.file_type='IMAGE'
                AND (? IS NULL OR o.order_id=?)
                AND (? IS NULL OR COALESCE(a.work_date,CAST(a.regist_date AS date))>=?)
                AND (? IS NULL OR COALESCE(a.work_date,CAST(a.regist_date AS date))<=?)
                AND (? IS NULL OR a.photo_category=?)
            """+OrderQuery.ACCESS_CONDITION+" ORDER BY a.attachment_id DESC OFFSET ? ROWS FETCH NEXT 101 ROWS ONLY",params);
    }
}
