// package com.kyouseipro.neo.abstracts;

// import java.util.List;
// import java.util.Map;

// import com.kyouseipro.neo.common.Enums.SqlMode;
// import com.kyouseipro.neo.interfaces.LogSqlProvider;
// import com.kyouseipro.neo.sql.common.SqlBuilder;
// import com.kyouseipro.neo.sql.model.SqlResult;
// import com.kyouseipro.neo.sql.model.TableMeta;
// import com.kyouseipro.neo.sql.repository.SqlRepository;

// public abstract class BaseRepository {

//     protected final SqlRepository sqlRepository;
//     protected final LogSqlProvider logProvider;

//     protected BaseRepository(SqlRepository sqlRepository, LogSqlProvider logProvider) {
//         this.sqlRepository = sqlRepository;
//         this.logProvider = logProvider;
//     }

//     protected Long insert(String table, Map<String,Object> req, String editor, String idKey){
//         req.put("editor", editor);

//         SqlResult result = SqlBuilder.buildSqlWithLog(
//             table,
//             req,
//             SqlMode.INSERT,
//             idKey,
//             "version",
//             logProvider
//         );

//         return sqlRepository.insert(
//             result.getSql(),
//             result.getParams(),
//             rs -> rs.getLong(toSnake(idKey))
//         );
//     }

//     protected int update(String table, Map<String,Object> req, String editor, String idKey){
//         req.put("editor", editor);

//         SqlResult result = SqlBuilder.buildSqlWithLog(
//             table,
//             req,
//             SqlMode.UPDATE,
//             idKey,
//             "version",
//             logProvider
//         );

//         return sqlRepository.updateRequired(
//             result.getSql(),
//             result.getParams(),
//             "更新に失敗しました"
//         );
//     }

//     protected int delete(String table, Map<String,Object> req, String editor, String idKey){
//         req.put("editor", editor);

//         SqlResult result = SqlBuilder.buildSqlWithLog(
//             table,
//             req,
//             SqlMode.DELETE,
//             idKey,
//             "version",
//             logProvider
//         );

//         return sqlRepository.updateRequired(
//             result.getSql(),
//             result.getParams(),
//             "削除に失敗しました"
//         );
//     }

//     public int deleteByIds(
//             String table,
//             String idKey,
//             List<?> ids,
//             String editor
//     ){
//         TableMeta meta = new TableMeta(table, idKey, "state", "version");

//         SqlResult result = SqlBuilder.buildDeleteByIds(
//             meta,
//             ids,
//             editor,
//             logProvider
//         );

//         return sqlRepository.updateRequired(
//             result.getSql(),
//             result.getParams(),
//             "削除に失敗しました"
//         );
//     }

//     private String toSnake(String camel){
//         return camel.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
//     }
// }