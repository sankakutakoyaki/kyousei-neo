package com.kyouseipro.neo.domain.master.page;

import java.util.Map;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.kyouseipro.neo.common.enums.code.State;
import com.kyouseipro.neo.common.enums.util.EnumUtil;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class MasterPageController  {
    /**
	 * 作業項目
	 * @param mv
	 * @return
	 */
	@GetMapping("/master")
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user')")
	public String getItemMaster() {
        return "fragments/pages/master/content :: content";
    }

    @GetMapping("/api/master/init/cache")
    @ResponseBody
    public Map<String, Object> initItemMaster() {
        return Map.of(
            "common", Map.of(
                "state", EnumUtil.toMap(State.class)
            ),
            "page", Map.of(
                // "genderComboList", EnumUtil.toCombo(Gender.class),
                // "bloodTypeComboList", EnumUtil.toCombo(BloodType.class)
            )
        );
    }
}