package com.kyouseipro.neo.controller.page;

import java.time.LocalDate;
import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.corporation.OfficeListEntity;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.entity.sales.OrderEntity;
import com.kyouseipro.neo.entity.sales.OrderItemEntity;
import com.kyouseipro.neo.entity.sales.OrderListEntity;
import com.kyouseipro.neo.service.common.ComboBoxService;
import com.kyouseipro.neo.service.document.HistoryService;
import com.kyouseipro.neo.service.personnel.EmployeeService;
import com.kyouseipro.neo.service.sales.OrderListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SalesPageController {
    private final OrderListService orderListService;
    private final EmployeeService employeeService;
    private final ComboBoxService comboBoxService;
    private final HistoryService historyService;

	@GetMapping("/order")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public ModelAndView getEmployee(ModelAndView mv, @AuthenticationPrincipal OidcUser principal) {
		mv.setViewName("layouts/main");
        mv.addObject("title", "受注");
        mv.addObject("headerFragmentName", "fragments/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/menu :: salesFragment");
        mv.addObject("bodyFragmentName", "contents/sales/order :: bodyFragment");
        mv.addObject("insertCss", "/css/sales/order.css");

		// ユーザー名
		String userName = principal.getAttribute("preferred_username");
		EmployeeEntity user = employeeService.getEmployeeByAccount(userName);
		mv.addObject("user", user);

        // 初期化されたエンティティ
        mv.addObject("formEntity", new OrderEntity());
        mv.addObject("itemEntity", new OrderItemEntity());

        // 初期表示用受注リスト取得
        List<OrderListEntity> origin = orderListService.getBetweenOrderEntity(LocalDate.now(), LocalDate.now());
        // List<OrderListEntity> origin = orderListService.getOrderList();
        mv.addObject("origin", origin);

        // コンボボックスアイテム取得
        List<SimpleData> primeConstractorComboList = comboBoxService.getPrimeConstractorList();
        mv.addObject("primeConstractorComboList", primeConstractorComboList);
        List<OfficeListEntity> officeList = comboBoxService.getOfficeList();
        mv.addObject("officeList", officeList);
        // List<SimpleData> itemClassComboList = comboBoxService.getItemClass();
        // mv.addObject("itemClassComboList", itemClassComboList);

        mv.addObject("deleteCode", Enums.state.DELETE.getCode());

        // 履歴保存
        historyService.saveHistory(userName, "orders", "閲覧", 0, "");
		
        return mv;
    }
}
