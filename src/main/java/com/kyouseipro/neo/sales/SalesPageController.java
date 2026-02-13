package com.kyouseipro.neo.sales;

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

import com.kyouseipro.neo.abstracts.controller.BaseController;
import com.kyouseipro.neo.common.ComboBoxService;
import com.kyouseipro.neo.common.Enums;
import com.kyouseipro.neo.common.simpledata.entity.SimpleData;
import com.kyouseipro.neo.corporation.office.entity.OfficeListEntity;
import com.kyouseipro.neo.corporation.staff.entity.StaffListEntity;
import com.kyouseipro.neo.personnel.employee.entity.EmployeeEntity;
import com.kyouseipro.neo.sales.order.entity.DeliveryStaffEntity;
import com.kyouseipro.neo.sales.order.entity.OrderEntity;
import com.kyouseipro.neo.sales.order.entity.OrderItemEntity;
import com.kyouseipro.neo.sales.order.entity.OrderListEntity;
import com.kyouseipro.neo.sales.order.entity.WorkContentEntity;
import com.kyouseipro.neo.sales.order.service.OrderItemService;
import com.kyouseipro.neo.sales.order.service.OrderListService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class SalesPageController extends BaseController {
    private final OrderListService orderListService;
    private final OrderItemService orderItemService;
    private final ComboBoxService comboBoxService;

	@GetMapping("/order")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public ModelAndView getOrder(ModelAndView mv, @AuthenticationPrincipal OidcUser principal, HttpSession session, HttpServletResponse response) throws IOException {
		mv.setViewName("layouts/main");
        mv.addObject("title", "受注");
        mv.addObject("headerFragmentName", "fragments/common/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/common/menu :: salesFragment");
        mv.addObject("bodyFragmentName", "contents/sales/order :: bodyFragment");
        mv.addObject("insertCss", "/css/sales/order.css");

        EmployeeEntity user = getLoginUser(session, response);
        if (user == null) return null; // リダイレクト済みなので処理は止まる

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
        mv.addObject("bodyFragmentName", "contents/sales/goods :: bodyFragment");
        mv.addObject("insertCss", "/css/sales/goods.css");

        EmployeeEntity user = getLoginUser(session, response);
        if (user == null) return null; // リダイレクト済みなので処理は止まる

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

        return mv;
    }
}
