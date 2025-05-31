package com.kyouseipro.neo.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.corporation.CompanyEntity;
import com.kyouseipro.neo.entity.corporation.OfficeEntity;
import com.kyouseipro.neo.entity.corporation.OfficeEntity;
import com.kyouseipro.neo.entity.corporation.OfficeEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OfficeRepository {
    private final SqlRepository sqlRepository;
    private final GenericRepository genericRepository;

    public Integer insertOffice(OfficeEntity office, String editor) {
        String sql =
            "DECLARE @InsertedRows TABLE (office_id INT);" +
            "INSERT INTO offices (office_id, office_name, name, name_kana, tel_number, fax_number, postal_code, full_address, email, web_address, version, state) " +
            "OUTPUT INSERTED.office_id INTO @InsertedRows " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);" +
            "INSERT INTO offices_log (office_id, editor, process, log_date, office_id, office_name, name, name_kana, tel_number, fax_number, postal_code, full_address, email, web_address, version, state) " +
            "SELECT o.office_id, ?, 'INSERT', CURRENT_TIMESTAMP, office_id, office_name, name, name_kana, tel_number, fax_number, postal_code, full_address, email, web_address, version, state " +
            "FROM offices o " +
            "INNER JOIN @InsertedRows ir ON o.office_id = ir.office_id;" +
            "SELECT office_id FROM @InsertedRows;";

        return sqlRepository.execSql(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, office.getCompany_id());
                pstmt.setString(2, office.getCompany_name());
                pstmt.setString(3, office.getName());
                pstmt.setString(4, office.getName_kana());
                pstmt.setString(5, office.getTel_number());
                pstmt.setString(6, office.getFax_number());
                pstmt.setString(7, office.getPostal_code());
                pstmt.setString(8, office.getFull_address());
                pstmt.setString(9, office.getEmail());
                pstmt.setString(10, office.getWeb_address());
                pstmt.setInt(11, office.getVersion());
                pstmt.setInt(12, office.getState());

                pstmt.setString(13, editor);

                boolean hasResultSet = pstmt.execute();

                if (hasResultSet) {
                    try (ResultSet rs = pstmt.getResultSet()) {
                        if (rs.next()) {
                            return rs.getInt("office_id");
                        }
                    }
                }
                return null;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public Integer updateOffice(OfficeEntity office, String editor) {
        String sql =
            "DECLARE @UpdatedRows TABLE (office_id INT);" +
            "UPDATE offices SET " +
            "office_id=?, office_name=?, name=?, name_kana=?, tel_number=?, fax_number=?, postal_code=?, full_address=?, email=?, web_address=?, version=?, state=? " +
            "OUTPUT INSERTED.office_id INTO @UpdatedRows " +
            "WHERE office_id=?;" +
            "INSERT INTO offices_log (office_id, editor, process, log_date, office_id, office_name, name, name_kana, tel_number, fax_number, postal_code, full_address, email, web_address, version, state) " +
            "SELECT o.office_id, ?, 'UPDATE', CURRENT_TIMESTAMP, office_id, office_name, name, name_kana, tel_number, fax_number, postal_code, full_address, email, web_address, version, state " +
            "FROM offices o " +
            "INNER JOIN @UpdatedRows ur ON o.office_id = ur.office_id;" +
            "SELECT office_id FROM @UpdatedRows;";

        return sqlRepository.execSql(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, office.getCompany_id());
                pstmt.setString(2, office.getCompany_name());
                pstmt.setString(3, office.getName());
                pstmt.setString(4, office.getName_kana());
                pstmt.setString(5, office.getTel_number());
                pstmt.setString(6, office.getFax_number());
                pstmt.setString(7, office.getPostal_code());
                pstmt.setString(8, office.getFull_address());
                pstmt.setString(9, office.getEmail());
                pstmt.setString(10, office.getWeb_address());
                pstmt.setInt(11, office.getVersion());
                pstmt.setInt(12, office.getState());

                pstmt.setInt(13, office.getOffice_id());  // WHERE句

                pstmt.setString(14, editor);

                boolean hasResultSet = pstmt.execute();

                if (hasResultSet) {
                    try (ResultSet rs = pstmt.getResultSet()) {
                        if (rs.next()) {
                            return rs.getInt("office_id");
                        }
                    }
                }
                return null;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    // office_idによる取得
    public OfficeEntity findById(int officeId) {
        return genericRepository.findOne(
        "SELECT * FROM offices WHERE office_id = ? AND NOT (state = ?)",
            ps -> {
                ps.setInt(1, officeId); // 1番目の ?
                ps.setInt(2, Enums.state.DELETE.getCode());     // 2番目の ?
            },
            OfficeEntity::new // Supplier<T>
        );
    }

    // 全件取得の例（必要に応じて）
    public List<OfficeEntity> findAll() {
        return genericRepository.findAll(
            "SELECT * FROM offices WHERE NOT (state = ?)",
            ps -> ps.setInt(1, Enums.state.DELETE.getCode()),
            OfficeEntity::new
        );
    }

    // 全件取得の例（必要に応じて）
    public List<OfficeEntity> findAllClient() {
        return genericRepository.findAll(
            "SELECT o.*, c.name as company_name, c.name_kana as company_name_kana FROM offices o" + 
            " INNER JOIN companies c ON c.company_id = o.company_id" + 
            " WHERE NOT (c.category = ?) AND NOT (c.state = ?) AND NOT (o.state = ?)",
            ps -> {
                ps.setInt(1, 0); // 1番目の ?
                ps.setInt(2, Enums.state.DELETE.getCode());     // 2番目の ?
                ps.setInt(3, Enums.state.DELETE.getCode());     // 3番目の ?
            },
            OfficeEntity::new
        );
    }
}

