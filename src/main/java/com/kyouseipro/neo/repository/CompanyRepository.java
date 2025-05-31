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
import com.kyouseipro.neo.entity.person.EmployeeEntity;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CompanyRepository {
    private final SqlRepository sqlRepository;
    private final GenericRepository genericRepository;

    public Integer insertCompany(CompanyEntity company, String editor) {
        String sql = 
            "DECLARE @InsertedRows TABLE (company_id INT);" +
            "INSERT INTO company (category, name, name_kana, tel_number, fax_number, postal_code, full_address, email, web_address, version, state) " +
            "OUTPUT INSERTED.company_id INTO @InsertedRows " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);" +

            "INSERT INTO company_log (company_id, editor, process, log_date, category, name, name_kana, tel_number, fax_number, postal_code, full_address, email, web_address, version, state) " +
            "SELECT c.company_id, ?, 'INSERT', CURRENT_TIMESTAMP, c.category, c.name, c.name_kana, c.tel_number, c.fax_number, c.postal_code, c.full_address, c.email, c.web_address, c.version, c.state " +
            "FROM company c INNER JOIN @InsertedRows ir ON c.company_id = ir.company_id;" +

            "SELECT company_id FROM @InsertedRows;";

            return sqlRepository.execSql(conn -> {
                try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                    pstmt.setInt(1, company.getCategory());
                    pstmt.setString(2, company.getName());
                    pstmt.setString(3, company.getName_kana());
                    pstmt.setString(4, company.getTel_number());
                    pstmt.setString(5, company.getFax_number());
                    pstmt.setString(6, company.getPostal_code());
                    pstmt.setString(7, company.getFull_address());
                    pstmt.setString(8, company.getEmail());
                    pstmt.setString(9, company.getWeb_address());
                    pstmt.setInt(10, company.getVersion());
                    pstmt.setInt(11, company.getState());

                    pstmt.setString(12, editor);  // ログ用エディタ

                    boolean hasResultSet = pstmt.execute();

                    if (hasResultSet) {
                        try (ResultSet rs = pstmt.getResultSet()) {
                            if (rs.next()) {
                                return rs.getInt("company_id");
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

    public Integer updateCompany(CompanyEntity company, String editor) {
        String sql = 
            "DECLARE @UpdatedRows TABLE (company_id INT);" +
            "UPDATE company SET " +
            "category=?, name=?, name_kana=?, tel_number=?, fax_number=?, postal_code=?, full_address=?, email=?, web_address=?, version=?, state=? " +
            "OUTPUT INSERTED.company_id INTO @UpdatedRows " +
            "WHERE company_id=?;" +

            "INSERT INTO company_log (company_id, editor, process, log_date, category, name, name_kana, tel_number, fax_number, postal_code, full_address, email, web_address, version, state) " +
            "SELECT c.company_id, ?, 'UPDATE', CURRENT_TIMESTAMP, c.category, c.name, c.name_kana, c.tel_number, c.fax_number, c.postal_code, c.full_address, c.email, c.web_address, c.version, c.state " +
            "FROM company c INNER JOIN @UpdatedRows ur ON c.company_id = ur.company_id;" +

            "SELECT company_id FROM @UpdatedRows;";

        return sqlRepository.execSql(conn -> {
            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, company.getCategory());
                pstmt.setString(2, company.getName());
                pstmt.setString(3, company.getName_kana());
                pstmt.setString(4, company.getTel_number());
                pstmt.setString(5, company.getFax_number());
                pstmt.setString(6, company.getPostal_code());
                pstmt.setString(7, company.getFull_address());
                pstmt.setString(8, company.getEmail());
                pstmt.setString(9, company.getWeb_address());
                pstmt.setInt(10, company.getVersion());
                pstmt.setInt(11, company.getState());

                pstmt.setInt(12, company.getCompany_id());  // WHERE句用ID

                pstmt.setString(13, editor);  // ログ用エディタ

                boolean hasResultSet = pstmt.execute();

                if (hasResultSet) {
                    try (ResultSet rs = pstmt.getResultSet()) {
                        if (rs.next()) {
                            return rs.getInt("company_id");
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

    // IDによる取得
    public CompanyEntity findById(int companyId) {
        return genericRepository.findOne(
        "SELECT * FROM companies WHERE company_id = ? AND NOT (state = ?)",
            ps -> {
                ps.setInt(1, companyId); // 1番目の ?
                ps.setInt(2, Enums.state.DELETE.getCode());     // 2番目の ?
            },
            CompanyEntity::new // Supplier<T>
        );
    }

    // 全件取得
    public List<CompanyEntity> findAll() {
        return genericRepository.findAll(
            "SELECT * FROM companies WHERE NOT (state = ?)",
            ps -> ps.setInt(1, Enums.state.DELETE.getCode()),
            CompanyEntity::new
        );
    }

    // 全件取得
    public List<CompanyEntity> findAllClient() {
        return genericRepository.findAll(
            "SELECT * FROM companies WHERE NOT (category = ?) AND NOT (state = ?)",
            ps -> {
                ps.setInt(1, 0); // 1番目の ?
                ps.setInt(2, Enums.state.DELETE.getCode());     // 2番目の ?
            },
            CompanyEntity::new
        );
    }
}
