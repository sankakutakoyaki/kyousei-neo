package com.kyouseipro.neo.domain.business.order.pdf.application;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.kyouseipro.neo.common.exception.BusinessException;
import com.kyouseipro.neo.domain.business.order.pdf.model.ConfirmedOrderCandidate;
import com.kyouseipro.neo.domain.business.order.pdf.repository.OrderImportRegistrationRepository;
import com.kyouseipro.neo.sql.provider.Tables;
import com.kyouseipro.neo.sql.repository.BaseSqlRepository;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class OrderImportRegistrationService {
    private final OrderImportRegistrationRepository imports;
    private final BaseSqlRepository base;
    private final ObjectMapper mapper;
    public record Result(long orderId, boolean alreadyRegistered) {}

    @Transactional
    public Result register(long importId, Map<String, String> values, String editor) {
        if (importId <= 0) throw new BusinessException("取込PDFの指定が不正です。");
        if (editor == null || editor.isBlank() || editor.length() > 100) throw new BusinessException("ログイン情報を確認できません。");
        var source = imports.lock(importId);
        // The import row serializes retries, including two simultaneous save requests.
        if (source.orderId() != null) return new Result(source.orderId(), true);
        if (source.shipperId() <= 0) throw new BusinessException("荷主が未設定です。");
        var candidate = ConfirmedOrderCandidate.parse(values, mapper);
        long reviewId = imports.reviewId(importId);
        String confirmed;
        try { confirmed = mapper.writeValueAsString(values); }
        catch (JsonProcessingException e) { throw new BusinessException("確認結果の保存形式が不正です。"); }
        Map<String, Object> order = new LinkedHashMap<>(candidate.order());
        order.put("primeConstractorId", source.shipperId());
        long orderId = base.insert(Tables.ORDER_BY_IDS, order, editor);
        for (var item : candidate.items()) {
            Map<String, Object> row = new LinkedHashMap<>(item);
            row.put("orderId", orderId);
            base.insert(Tables.ORDER_ITEM_BY_IDS, row, editor);
        }
        for (var work : candidate.works()) {
            Map<String, Object> row = new LinkedHashMap<>(work);
            row.put("orderId", orderId);
            base.insert(Tables.ORDER_WORK_BY_IDS, row, editor);
        }
        imports.finish(importId, orderId, reviewId, confirmed, editor);
        return new Result(orderId, false);
    }
}
