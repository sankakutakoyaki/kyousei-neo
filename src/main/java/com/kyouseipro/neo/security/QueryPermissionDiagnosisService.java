package com.kyouseipro.neo.security;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.kyouseipro.neo.common.enums.system.QueryId;
import com.kyouseipro.neo.sql.model.QueryDefinition;
import com.kyouseipro.neo.sql.provider.SqlProvider;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class QueryPermissionDiagnosisService {

    private final SqlProvider sqlProvider;
    private final QueryPermissionValidator validator;

    public List<PermissionIssue> diagnose() {
        List<PermissionIssue> issues = new ArrayList<>();

        for (Map.Entry<QueryId, QueryDefinition> entry: sqlProvider.getDefinitions().entrySet()) {

            QueryId queryId = entry.getKey();
            QueryDefinition definition = entry.getValue();

            for (String message : validator.validate(definition)) {
                issues.add(new PermissionIssue(queryId.getId(), message));
            }
        }

        issues.sort(
            Comparator.comparing(PermissionIssue::queryId)
                .thenComparing(PermissionIssue::message)
        );

        return List.copyOf(issues);
    }

    public record PermissionIssue(String queryId, String message) {}
}