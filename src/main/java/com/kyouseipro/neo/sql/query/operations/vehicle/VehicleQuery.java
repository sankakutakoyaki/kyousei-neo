package com.kyouseipro.neo.sql.query.operations.vehicle;

import java.util.List;

import com.kyouseipro.neo.sql.model.CsvColumn;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.common.enums.code.Transmission;

public class VehicleQuery {

    public static QueryDefinition vehicleList() {
        return QueryDefinition.select(
            """
            SELECT
                v.vehicle_id,
                v.office_id,
                COALESCE(o.name, '') AS office_name,
                v.vehicle_name,
                v.manufacturer,
                v.model_code,
                v.registration_area,
                v.registration_class,
                v.registration_kana,
                v.registration_number,
                v.first_registration_year,
                v.first_registration_month,
                v.inspection_expiration_date,
                v.transmission,
                v.front_tire_size,
                v.rear_tire_size,
                v.vehicle_height,
                v.maximum_load,
                v.version,
                v.state
            FROM vehicles v
            LEFT OUTER JOIN offices o
                ON o.office_id = v.office_id
                AND o.state = ?
            WHERE v.state = ?
            """,
            List.of("state", "state")
        );
    }

    public static QueryDefinition vehicleDetail() {
        return QueryDefinition.select(
            """
            SELECT
                v.vehicle_id,
                v.office_id,
                COALESCE(o.name, '') AS office_name,
                v.vehicle_name,
                v.manufacturer,
                v.model_code,
                v.registration_area,
                v.registration_class,
                v.registration_kana,
                v.registration_number,
                v.first_registration_year,
                v.first_registration_month,
                v.inspection_expiration_date,
                v.transmission,
                v.front_tire_size,
                v.rear_tire_size,
                v.vehicle_height,
                v.maximum_load,
                v.remarks,
                v.version,
                v.state
            FROM vehicles v
            LEFT OUTER JOIN offices o
                ON o.office_id = v.office_id
                AND o.state = ?
            WHERE v.state = ?
              AND v.vehicle_id = ?
            """,
            List.of("state", "state", "vehicleId")
        );
    }

    public static QueryDefinition vehicleCsv() {

        String sql = """
            SELECT
                vehicle_id,
                office_id,
                vehicle_name,
                manufacturer,
                model_code,
                registration_area,
                registration_class,
                registration_kana,
                registration_number,
                first_registration_year,
                first_registration_month,
                inspection_expiration_date,
                transmission,
                front_tire_size,
                rear_tire_size,
                vehicle_height,
                maximum_load,
                remarks
            FROM vehicles
            WHERE state = ?
              AND vehicle_id IN (:ids)
            """;

        List<String> params = List.of("state", "ids");

        List<CsvColumn> columns = List.of(
            new CsvColumn("vehicleId", "車両ID"),
            new CsvColumn("officeId", "営業所ID"),
            new CsvColumn("vehicleName", "車種"),
            new CsvColumn("manufacturer", "メーカー"),
            new CsvColumn("modelCode", "型式"),
            new CsvColumn("registrationArea", "地域名"),
            new CsvColumn("registrationClass", "分類番号"),
            new CsvColumn("registrationKana", "かな"),
            new CsvColumn("registrationNumber", "一連指定番号"),
            new CsvColumn("firstRegistrationYear", "初度登録年"),
            new CsvColumn("firstRegistrationMonth", "初度登録月"),
            new CsvColumn("inspectionExpirationDate", "車検期限"),
            new CsvColumn("transmission", "ミッション", Transmission.class),
            new CsvColumn("frontTireSize", "前輪タイヤ"),
            new CsvColumn("rearTireSize", "後輪タイヤ"),
            new CsvColumn("vehicleHeight", "車高(mm)"),
            new CsvColumn("maximumLoad", "最大積載量(kg)"),
            new CsvColumn("remarks", "備考")
        );

        return QueryDefinition.csv(sql, params, columns);
    }
}