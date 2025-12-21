package com.kyouseipro.neo.controller.page;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.entity.corporation.OfficeListEntity;
import com.kyouseipro.neo.entity.corporation.StaffListEntity;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.entity.sales.DeliveryStaffEntity;
import com.kyouseipro.neo.entity.sales.KsSalesEntity;
import com.kyouseipro.neo.entity.sales.OrderEntity;
import com.kyouseipro.neo.entity.sales.OrderItemEntity;
import com.kyouseipro.neo.entity.sales.OrderListEntity;
import com.kyouseipro.neo.entity.sales.WorkContentEntity;
import com.kyouseipro.neo.service.common.ComboBoxService;
import com.kyouseipro.neo.service.document.HistoryService;
import com.kyouseipro.neo.service.personnel.EmployeeService;
import com.kyouseipro.neo.service.sales.KsSalesService;
import com.kyouseipro.neo.service.sales.OrderItemService;
import com.kyouseipro.neo.service.sales.OrderListService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SalesPageController {
    private final OrderListService orderListService;
    private final OrderItemService orderItemService;
    private final EmployeeService employeeService;
    private final KsSalesService ksSalesService;
    private final ComboBoxService comboBoxService;
    private final HistoryService historyService;

	@GetMapping("/order")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public ModelAndView getOrder(ModelAndView mv, @AuthenticationPrincipal OidcUser principal) {
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
        mv.addObject("staffEntity", new DeliveryStaffEntity());
        mv.addObject("workEntity", new WorkContentEntity());

        // 初期表示用受注リスト取得
        List<OrderListEntity> origin = orderListService.getBetweenOrderEntity(LocalDate.now(), LocalDate.now());
        mv.addObject("origin", origin);

        // コンボボックスアイテム取得
        List<SimpleData> primeConstractorComboList = comboBoxService.getPrimeConstractorList();
        mv.addObject("primeConstractorComboList", primeConstractorComboList);
        List<OfficeListEntity> officeList = comboBoxService.getOfficeListOrderByKana();
        mv.addObject("officeList", officeList);
        List<StaffListEntity> salesStaffList = comboBoxService.getSalesStaffList();
        mv.addObject("salesStaffList", salesStaffList);

        mv.addObject("deleteCode", Enums.state.DELETE.getCode());

        // 履歴保存
        historyService.saveHistory(userName, "orders", "閲覧", 0, "");
		
        return mv;
    }

    @GetMapping("/goods")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public ModelAndView getGoods(ModelAndView mv, @AuthenticationPrincipal OidcUser principal) {
		mv.setViewName("layouts/main");
        mv.addObject("title", "入荷");
        mv.addObject("headerFragmentName", "fragments/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/menu :: salesFragment");
        mv.addObject("bodyFragmentName", "contents/sales/goods :: bodyFragment");
        mv.addObject("insertCss", "/css/sales/goods.css");

		// ユーザー名
		String userName = principal.getAttribute("preferred_username");
		EmployeeEntity user = employeeService.getEmployeeByAccount(userName);
		mv.addObject("user", user);

        // // 初期化されたエンティティ
        mv.addObject("formEntity", new OrderItemEntity());

        // 初期表示用受注リスト取得
        List<OrderItemEntity> origin = orderItemService.getBetweenOrderItemEntity(LocalDate.now(), LocalDate.now());
        mv.addObject("origin", origin);

        // コンボボックスアイテム取得
        List<SimpleData> companyComboList = comboBoxService.getPrimeConstractorList();
        mv.addObject("companyComboList", companyComboList);
        List<SimpleData> shippingCompanyComboList = comboBoxService.getCompanyListByCategory(Enums.clientCategory.TRANSPORT.getCode());
        mv.addObject("shippingCompanyComboList", shippingCompanyComboList);
        List<SimpleData> supplierComboList = comboBoxService.getCompanyListByCategory(Enums.clientCategory.SUPPLIER.getCode());
        mv.addObject("supplierComboList", supplierComboList);
        List<SimpleData> classificationComboList = comboBoxService.getItemClass();
        mv.addObject("classificationComboList", classificationComboList);
        List<OfficeListEntity> officeList = comboBoxService.getOfficeListOrderByKana();
        mv.addObject("officeList", officeList);

        mv.addObject("deleteCode", Enums.state.DELETE.getCode());
        mv.addObject("goodsCode", Enums.ItemClass.GOODS.getCode());
        mv.addObject("materialsCode", Enums.ItemClass.MATERIALS.getCode());
        mv.addObject("equipmentCode", Enums.ItemClass.EQUIPMENT.getCode());
        mv.addObject("returnsCode", Enums.ItemClass.RETURNS.getCode());

        // 履歴保存
        historyService.saveHistory(userName, "order_items", "閲覧", 0, "");
		
        return mv;
    }

    @GetMapping("/ks/sales")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public ModelAndView getKsSales(ModelAndView mv, @AuthenticationPrincipal OidcUser principal) {
		mv.setViewName("layouts/main");
        mv.addObject("title", "ケーズ");
        mv.addObject("headerFragmentName", "fragments/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/menu :: salesFragment");
        mv.addObject("bodyFragmentName", "contents/sales/ks :: bodyFragment");
        mv.addObject("insertCss", "/css/sales/ks.css");

		// ユーザー名
		String userName = principal.getAttribute("preferred_username");
		EmployeeEntity user = employeeService.getEmployeeByAccount(userName);
		mv.addObject("user", user);

        LocalDate now = LocalDate.now();
        LocalDate monthStart = now.withDayOfMonth(1);
        LocalDate monthEnd   = now.withDayOfMonth(now.lengthOfMonth());
        // 初期表示用受注リスト取得
        List<KsSalesEntity> origin01 = ksSalesService.getBetweenKsSalesEntity(monthStart, monthEnd.plusDays(1), "staff");
        mv.addObject("origin01", origin01);

        List<String> storeNames = origin01.stream().map(KsSalesEntity::getStore_name).filter(Objects::nonNull).distinct().toList();
        mv.addObject("storeComboList01", storeNames);

        // 履歴保存
        historyService.saveHistory(userName, "ks_sales", "閲覧", 0, "");
		
        return mv;
    }
}
