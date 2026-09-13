package com.kyouseipro.neo.domain.operations;

import java.util.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.ui.Model;
import org.springframework.security.access.prepost.PreAuthorize;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Controller @RequiredArgsConstructor
@PreAuthorize("hasAnyAuthority('APPROLE_admin','APPROLE_master','APPROLE_leader','APPROLE_staff','APPROLE_user')")
public class OperationPageController {
    private final OperationAccess access;
    @GetMapping("/operations") public String menu() { return "fragments/pages/index/operations"; }
    @GetMapping({"/vehicles","/vehicle-maintenance","/daily-crews","/driving-records","/qualification-types","/qualifications","/labor","/health-checks"})
    public String page(HttpServletRequest request,Model model) {
        String route=request.getServletPath().substring(1);
        var spec=Arrays.stream(OperationSpec.values()).filter(s->s.route.equals(route)).findFirst().orElseThrow();
        access.read(spec);
        String view=Objects.toString(request.getParameter("view"),"all");
        model.addAttribute("opView",Set.of("all","renewal","expired").contains(view)?view:"all");
        model.addAttribute("opEntity",spec.name()); model.addAttribute("opTitle",spec.title);
        model.addAttribute("opSection",spec.section);
        model.addAttribute("opCanWrite",access.canWrite(spec)); model.addAttribute("opPrivate",access.privateAccess());
        model.addAttribute("opMobileEditor",access.manager() && access.canWrite(spec));
        model.addAttribute("opFields",spec.fields.stream().map(f->{
            var m=new HashMap<String,Object>();m.put("text",f.label());m.put("name",OperationSpec.snake(f.name()).replace('_','-'));m.put("id","op-"+f.name());
            m.put("type",f.type());if(f.required())m.put("required",f.label()+"を入力してください。");m.put("key",f.name());return m;
        }).toList());
        return "fragments/pages/operations/content :: content";
    }
    @GetMapping("/dispatch") public String dispatch(Model model, @org.springframework.web.bind.annotation.RequestParam(defaultValue="0") long orderId) {
        model.addAttribute("opOrderId",orderId);
        model.addAttribute("opMobileEditor",access.manager());
        return "fragments/pages/operations/dispatch :: content";
    }
}
