package com.kyouseipro.neo.domain.business.order.ocr;

import java.util.Map;

/** 株式会社平和堂（company_id=1085）帳票の、150dpi帳票プレビュー上での初期読取範囲。 */
public final class HeiwadoOcrDefaultLayout {

    public static final long PRIME_CONSTRACTOR_ID = 1085L;

    public static final Map<String, OcrRegion> REGIONS = Map.of(
        "customerName", new OcrRegion(125, 315, 390, 65),
        "mobilePhone", new OcrRegion(700, 320, 290, 65),
        "address", new OcrRegion(125, 380, 850, 90),
        "itemModel1", new OcrRegion(305, 820, 340, 65),
        "itemModel2", new OcrRegion(305, 885, 340, 65),
        "requestedDate", new OcrRegion(660, 1060, 310, 65),
        "contactNote", new OcrRegion(660, 1125, 330, 145)
    );

    private HeiwadoOcrDefaultLayout() {
    }

    public record OcrRegion(int x, int y, int width, int height) {
    }
}
