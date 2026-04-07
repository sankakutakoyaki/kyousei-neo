package com.kyouseipro.neo.corporation.company.repository;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.Enums.SqlMode;
import com.kyouseipro.neo.corporation.company.entity.CompanyEntity;
import com.kyouseipro.neo.corporation.company.mapper.CompanyEntityMapper;
import com.kyouseipro.neo.dto.IdListRequest;
import com.kyouseipro.neo.interfaces.LogSqlProvider;
import com.kyouseipro.neo.sql.common.SqlBuilder;
import com.kyouseipro.neo.sql.model.SqlResult;
import com.kyouseipro.neo.sql.model.TableMeta;
import com.kyouseipro.neo.sql.repository.BaseRepository;
import com.kyouseipro.neo.sql.repository.SqlRepository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class CompanyRepository {
    private final SqlRepository sqlRepository;
    private final BaseRepository baseRepository;
    // private final LogSqlProvider logProvider = new CompanyLogSqlProvider();

    // public CompanyRepository(SqlRepository sqlRepository){
    //     super(sqlRepository, new CompanyLogSqlProvider());
    // }

    /**
     * IDによる取得。
     * @param id
     * @return IDから取得したEntityを返す。
     */
    public CompanyEntity findById(Long id) {
        return sqlRepository.queryOne(
            """
            SELECT * FROM companies WHERE company_id = ? AND NOT (state = ?)
            """,
            (ps, v) -> {
                int index = 1;
                ps.setLong(index++, id);
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            CompanyEntityMapper::map
        );
    }

    /**
     * 全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<CompanyEntity> findAll() {
        return sqlRepository.queryList(
            """
            SELECT * FROM companies WHERE NOT (state = ?)
            """,
            (ps, v) -> ps.setInt(1, Enums.state.DELETE.getCode()),
            CompanyEntityMapper::map
        );
    }

    /**
     * 荷主全件取得。
     * 0件の場合は空リストを返す。
     * @return 取得したリストを返す
     */
    public List<CompanyEntity> findAllClient() {
        return sqlRepository.queryList(
            """
            SELECT * FROM companies WHERE NOT (category = ?) AND NOT (state = ?)
            """,
            (ps, v) -> {
                int index = 1;
                ps.setInt(index++, 0);
                ps.setInt(index++, Enums.state.DELETE.getCode());
            },
            CompanyEntityMapper::map
        );
    }

    // /**
    //  * 新規作成。
    //  * @param entity
    //  * @return 新規IDを返す。
    //  */
    // public Long insert(Map<String, Object> req, String editor) {
    //     req.put("editor", editor);
    //     req.putIfAbsent("category", Enums.clientCategory.PARTNER.getCode());
    //     SqlResult result = SqlBuilder.buildSqlWithLog(
    //         "companies",
    //         req,
    //         SqlMode.INSERT,
    //         "companyId",
    //         "version",
    //         logProvider
    //     );
    //     return sqlRepository.insert(
    //         result.getSql(),
    //         result.getParams(),
    //         rs -> rs.getLong("company_id")
    //     );
    // }
    
    // /**
    //  * 更新。
    //  * @param entity
    //  * @return 成功件数を返す。
    //  */
    // public int update(Map<String, Object> req, String editor) {
    //     req.put("editor", editor);
    //     SqlResult result = SqlBuilder.buildSqlWithLog(
    //         "companies",
    //         req,
    //         SqlMode.UPDATE,
    //         "companyId",
    //         "version",
    //         logProvider
    //     );
    //     return sqlRepository.updateRequired(
    //         result.getSql(),
    //         result.getParams(),
    //         "更新に失敗しました"
    //     );
    // }

    // /**
    //  * 単体論理削除
    //  * @param req
    //  * @param editor
    //  * @return
    //  */
    // public int delete(Map<String, Object> req, String editor) {
    //     req.put("editor", editor);
    //     SqlResult result = SqlBuilder.buildSqlWithLog(
    //         "companies",
    //         req,
    //         SqlMode.DELETE,
    //         "companyId",
    //         "version",
    //         logProvider
    //     );
    //     return sqlRepository.updateRequired(
    //         result.getSql(),
    //         result.getParams(),
    //         "削除に失敗しました"
    //     );
    // }

    public Long insert(Map<String,Object> req, String editor){
        // return baseRepository.insert("companies", req, editor, "companyId");
        return null;
    }

    public int update(Map<String,Object> req, String editor){
        // return baseRepository.update("companies", req, editor, "companyId");
        return 0;
    }

    public int delete(Map<String,Object> req, String editor){
        // return baseRepository.delete("companies", req, editor, "companyId");
        return 0;
    }

    public int deleteByIds(IdListRequest list, String editor){
        // return baseRepository.deleteByIds("companies", "companyId", list.getIds(), editor);
        return 0;
    }

    /**
     * IDで指定したENTITYを論理削除。
     * @param ids
     * @param editor
     * @return 成功件数を返す。
     */
    // public int deleteByIds(IdListRequest list, String editor) {
    //     TableMeta META =
    //         new TableMeta("companies", "company_id", "state");

    //     SqlResult result = SqlBuilder.buildDeleteByIds(
    //         META,
    //         list.getIds(),
    //         editor,
    //         logProvider
    //     );

    //     return sqlRepository.updateRequired(
    //         result.getSql(),
    //         result.getParams(),
    //         "削除に失敗しました"
    //     );
    // }

    /**
     * IDで指定したENTITYのCSVファイルをダウンロードする。
     * @param ids
     * @param editor
     * @return Idsで選択したEntityリストを返す。
     */
    public List<CompanyEntity> downloadCsvByIds(IdListRequest list, String editor) {
        String sql = CompanySqlBuilder.buildDownloadCsvByIds(list.getIds().size());

        if (list == null || list.getIds().isEmpty()) {
            throw new IllegalArgumentException("ダウンロード対象が指定されていません");
        }

        return sqlRepository.queryList(
            sql,
            (ps, v) -> CompanyParameterBinder.bindDownloadCsvByIds(ps, list.getIds()),
            CompanyEntityMapper::map
        );
    }
}
