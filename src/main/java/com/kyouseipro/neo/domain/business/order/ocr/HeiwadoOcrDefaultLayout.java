package com.kyouseipro.neo.domain.business.order.ocr;

import java.util.Map;

/** 株式会社平和堂（company_id=1085）帳票の、300dpi画像上での読取範囲。 */
public final class HeiwadoOcrDefaultLayout {

    public static final long PRIME_CONSTRACTOR_ID = 1085L;

    public static final Map<String, OcrRegion> REGIONS = Map.of(
        "customerName", new OcrRegion(800, 1220, 1050, 180),
        "mobilePhone", new OcrRegion(1540, 1180, 820, 210),
        "address", new OcrRegion(420, 1440, 1900, 430),
        "itemModel1", new OcrRegion(560, 1510, 860, 180),
        "itemModel2", new OcrRegion(560, 1700, 860, 180),
        "requestedDate", new OcrRegion(1500, 1860, 780, 180),
        "contactNote", new OcrRegion(1360, 1980, 1050, 450)
    );

    private HeiwadoOcrDefaultLayout() {
    }

    public record OcrRegion(int x, int y, int width, int height) {
    }
}
