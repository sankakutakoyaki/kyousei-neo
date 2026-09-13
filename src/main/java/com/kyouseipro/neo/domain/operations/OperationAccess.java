package com.kyouseipro.neo.domain.operations;

import java.util.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Component @RequiredArgsConstructor
public class OperationAccess {
    private final HttpServletRequest request;
    public boolean has(String... roles) {
        var auth=SecurityContextHolder.getContext().getAuthentication();
        return auth!=null && auth.getAuthorities().stream().anyMatch(a -> Arrays.asList(roles).contains(a.getAuthority()));
    }
    public boolean privateAccess() { return has("APPROLE_admin","APPROLE_master"); }
    public boolean manager() { return has("APPROLE_admin","APPROLE_master","APPROLE_leader"); }
    public String editor() {
        var auth=SecurityContextHolder.getContext().getAuthentication();
        if(auth==null) throw new AccessDeniedException("認証が必要です。");
        return auth.getName();
    }
    public void read(OperationSpec spec) {
        if(!has("APPROLE_admin","APPROLE_master","APPROLE_leader","APPROLE_staff","APPROLE_user") || (spec.sensitive() && !privateAccess()))
            throw new AccessDeniedException("この情報を参照する権限がありません。");
    }
    public boolean canWrite(OperationSpec spec) {
        return switch(spec) { case LABOR, HEALTH, QUALIFICATION, QUALIFICATION_TYPE -> privateAccess(); case CREW -> has("APPROLE_admin","APPROLE_master","APPROLE_leader","APPROLE_staff","APPROLE_user"); default -> manager(); };
    }
    public void write(OperationSpec spec, Map<String,Object> p) {
        read(spec);
        if(!canWrite(spec)) throw new AccessDeniedException("編集権限がありません。");
        if(!(p.get("mobile") instanceof Boolean)) throw new AccessDeniedException("画面を開き直してください。");
        String agent=Objects.toString(request.getHeader("User-Agent"),"").toLowerCase(Locale.ROOT);
        boolean mobile=Boolean.TRUE.equals(p.get("mobile")) || "?1".equals(request.getHeader("Sec-CH-UA-Mobile")) || agent.matches(".*(android|iphone|ipad|ipod|mobile).*");
        if(mobile && !manager()) throw new AccessDeniedException("スマホでの編集には管理権限が必要です。");
    }
}
