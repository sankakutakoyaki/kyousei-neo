package com.kyouseipro.neo.domain.management;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MyOrderPageController {

    @GetMapping("/my-orders")
    @PreAuthorize("hasAuthority('APPROLE_user')")
    public String getMyOrders() {
        return "fragments/pages/business/myorder/content :: content";
    }
}