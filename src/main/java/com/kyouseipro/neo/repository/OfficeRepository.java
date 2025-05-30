package com.kyouseipro.neo.repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.entity.corporation.OfficeEntity;
import com.kyouseipro.neo.entity.corporation.OfficeEntity;
import com.kyouseipro.neo.entity.corporation.OfficeEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class OfficeRepository {
    private final SqlRepository sqlRepository;

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
    public OfficeEntity findById(int office_id) {
        String sql = "SELECT * FROM offices WHERE office_id = ?";

        return sqlRepository.execSql(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, office_id);
                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        OfficeEntity office = new OfficeEntity();
                        office.setEntity(rs);
                        return office;
                    }
                }
                return null;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    // 全件取得の例（必要に応じて）
    public List<OfficeEntity> findAll() {
        String sql = "SELECT * FROM offices";

        return sqlRepository.execSql(conn -> {
            List<OfficeEntity> list = new ArrayList<>();
            try (PreparedStatement pstmt = conn.prepareStatement(sql);
                 ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    OfficeEntity office = new OfficeEntity();
                    office.setEntity(rs);
                    list.add(office);
                }
                return list;
            } catch (SQLException e) {
                e.printStackTrace();
                return null;
            }
        });
    }
}

