package com.kyouseipro.neo.controller.page;

import java.io.IOException;
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
import com.kyouseipro.neo.controller.abstracts.BaseController;
import com.kyouseipro.neo.entity.corporation.OfficeListEntity;
import com.kyouseipro.neo.entity.corporation.StaffListEntity;
import com.kyouseipro.neo.entity.dto.SimpleData;
import com.kyouseipro.neo.entity.order.DeliveryStaffEntity;
import com.kyouseipro.neo.entity.order.OrderEntity;
import com.kyouseipro.neo.entity.order.OrderItemEntity;
import com.kyouseipro.neo.entity.order.OrderListEntity;
import com.kyouseipro.neo.entity.order.WorkContentEntity;
import com.kyouseipro.neo.entity.personnel.EmployeeEntity;
import com.kyouseipro.neo.service.common.ComboBoxService;
import com.kyouseipro.neo.service.dto.HistoryService;
import com.kyouseipro.neo.service.order.OrderItemService;
import com.kyouseipro.neo.service.order.OrderListService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class OrderPageController extends BaseController {
    private final OrderListService orderListService;
    private final OrderItemService orderItemService;
    private final ComboBoxService comboBoxService;
    private final HistoryService historyService;

	@GetMapping("/order")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public ModelAndView getOrder(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session, HttpServletResponse response) throws IOException {
		mv.setViewName("layouts/main");
        mv.addObject("title", "受注");
        mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/common/menu :: salesFragment");
        mv.addObject("bodyFragmentName", "contents/order/order :: bodyFragment");
        mv.addObject("insertCss", "/css/order/order.css");

        EmployeeEntity user = getLoginUser(session, response);
        if (user == null) return null; // リダイレクト済みなので処理は止まる
		// ユーザー名
		// String userName = principal.getAttribute("preferred_username");
		// EmployeeEntity user = employeeService.getByAccount(userName);
		// mv.addObject("user", user);
        // mv.addObject("user", employeeService.getByAccount(userName).orElse(null));

        // 初期化されたエンティティ
        mv.addObject("formEntity", new OrderEntity());
        mv.addObject("itemEntity", new OrderItemEntity());
        mv.addObject("staffEntity", new DeliveryStaffEntity());
        mv.addObject("workEntity", new WorkContentEntity());

        // 初期表示用受注リスト取得
        List<OrderListEntity> origin = orderListService.getBetween(LocalDate.now(), LocalDate.now());
        mv.addObject("origin", origin);

        // コンボボックスアイテム取得
        List<SimpleData> primeConstractorComboList = comboBoxService.getPrimeConstractorList();
        mv.addObject("primeConstractorComboList", primeConstractorComboList);
        List<OfficeListEntity> officeList = comboBoxService.getOfficeListOrderByKana();
        mv.addObject("officeList", officeList);
        List<StaffListEntity> salesStaffList = comboBoxService.getSalesStaffList();
        mv.addObject("salesStaffList", salesStaffList);

        mv.addObject("deleteCode", Enums.state.DELETE.getCode());

        // EmployeeEntity user = getLoginUser(session);
        historyService.save(user.getAccount(), "orders", "閲覧", 0, "");
		
        return mv;
    }

    @GetMapping("/goods")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public ModelAndView getGoods(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session, HttpServletResponse response) throws IOException {
		mv.setViewName("layouts/main");
        mv.addObject("title", "入荷");
        mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/common/menu :: salesFragment");
        mv.addObject("bodyFragmentName", "contents/order/goods :: bodyFragment");
        mv.addObject("insertCss", "/css/order/goods.css");

        EmployeeEntity user = getLoginUser(session, response);
        if (user == null) return null; // リダイレクト済みなので処理は止まる
		// ユーザー名
		// String userName = principal.getAttribute("preferred_username");
		// EmployeeEntity user = employeeService.getByAccount(userName);
		// mv.addObject("user", user);
        // mv.addObject("user", employeeService.getByAccount(userName).orElse(null));

        // // 初期化されたエンティティ
        mv.addObject("formEntity", new OrderItemEntity());

        // 初期表示用受注リスト取得
        List<OrderItemEntity> origin = orderItemService.getBetween(LocalDate.now(), LocalDate.now());
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

        // EmployeeEntity user = getLoginUser(session);
        historyService.save(user.getAccount(), "order_items", "閲覧", 0, "");
		
        return mv;
    }
}
