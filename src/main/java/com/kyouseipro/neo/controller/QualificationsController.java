package com.kyouseipro.neo.controller;

import java.util.List;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import com.kyouseipro.neo.component.UploadConfig;
import com.kyouseipro.neo.entity.data.SimpleData;
import com.kyouseipro.neo.entity.person.EmployeeEntity;
import com.kyouseipro.neo.entity.person.QualificationFilesEntity;
import com.kyouseipro.neo.entity.person.QualificationsEntity;
import com.kyouseipro.neo.interfaceis.IEntity;
import com.kyouseipro.neo.service.ComboBoxService;
import com.kyouseipro.neo.service.DatabaseService;
import com.kyouseipro.neo.service.FileService;
import com.kyouseipro.neo.service.personnel.EmployeeService;
import com.kyouseipro.neo.service.personnel.QualificationsService;

import lombok.RequiredArgsConstructor;

@Controller
@RequiredArgsConstructor
public class QualificationsController {
    private final EmployeeService employeeService;
    private final ComboBoxService comboBoxService;
    private final DatabaseService databaseService;
    private final QualificationsService qualificationsService;
    private final FileService fileService;
    /**
	 * 従業員
	 * @param mv
	 * @return
	 */
	@GetMapping("/qualifications")
	@ResponseBody
	@PreAuthorize("hasAnyAuthority('APPROLE_admin', 'APPROLE_master', 'APPROLE_leader', 'APPROLE_staff', 'APPROLE_user', 'APPROLE_office')")
	public ModelAndView getQualifications(ModelAndView mv, @AuthenticationPrincipal OidcUser principal) {
		mv.setViewName("layouts/main");
        mv.addObject("title", "資格");
        mv.addObject("headerFragmentName", "fragments/header :: headerFragment");
		mv.addObject("sidebarFragmentName", "fragments/menu :: personnelFragment");
        mv.addObject("bodyFragmentName", "contents/personnel/qualifications :: bodyFragment");
        mv.addObject("insertCss", "/css/personnel/qualifications.css");
        // mv.addObject("uploadFilePath", UploadConfig.getUploadDir());
		// ユーザー名
		String userName = principal.getAttribute("preferred_username");
		EmployeeEntity user = (EmployeeEntity) employeeService.getEmployeeByAccount(userName);
		mv.addObject("user", user);

        // 初期化されたエンティティ
        mv.addObject("formEntity", new QualificationsEntity());

        // 初期表示用資格情報リスト取得
        List<IEntity> origin = qualificationsService.getQualificationsList();
        mv.addObject("origin", origin);

        // コンボボックスアイテム取得
        List<IEntity> qualificationComboList = comboBoxService.getQualificationMaster();
        mv.addObject("qualificationComboList", qualificationComboList);

        // 履歴保存
        databaseService.saveHistory(userName, "qualifications", "閲覧", 200, "");
		
        return mv;
    }

    /**
     * IDから資格情報を取得する
     * @param ID
     * @return 
     */
    @PostMapping("/qualifications/get/employeeid")
	@ResponseBody
    public List<IEntity> getQualificationsById(@RequestParam int id) {
        return qualificationsService.getQualificationsByEmployeeId(id);
    }

    /**
     * IDから資格のファイルを取得する
     * @param ID
     * @return 
     */
    @PostMapping("/qualifications/get/files")
	@ResponseBody
    public List<IEntity> getQualificationsFilesById(@RequestParam int id) {
        return qualificationsService.getQualificationsFilesById(id);
    }

    /**
     * URLから資格のファイルを削除する
     * @param ID
     * @return 
     */
    @PostMapping("/qualifications/delete/files")
	@ResponseBody
    public IEntity deleteQualificationsFilesByUrl(@RequestParam String url) {
        SimpleData result = (SimpleData) fileService.deleteFile(UploadConfig.getUploadDir() + url);
        if (result.getNumber() > 0) {
            return qualificationsService.deleteQualificationsFilesByUrl(UploadConfig.getUploadDir() + url);
        } else {
            SimpleData simpleData = new SimpleData();
            simpleData.setNumber(0);
            return simpleData;
        }
    }

    /**
     * 資格情報のPDFファイルをアップロードする
     * @param files
     * @param folderName
     * @param id
     * @return
     */
    @PostMapping("/qualifications/file/upload")
    @ResponseBody
    public List<IEntity> fileUpload(@RequestParam("files") MultipartFile[] files, @RequestParam("folder_name") String folderName, @RequestParam("id") int id) {
        QualificationFilesEntity entity = new QualificationFilesEntity();
        entity.setQualifications_id(id);

        fileService.fileUpload(files, folderName, entity);
        return qualificationsService.getQualificationsFilesById(id);
        // return result;
    }

    /**
     * すべての資格情報を取得する
     * @return
     */
    @GetMapping("/qualifications/get/all")
	@ResponseBody
    public List<IEntity> getQualificationsList() {
        return qualificationsService.getQualificationsList();
    }

    /**
     * IDリストの資格情報を保存する
     * @param IDS
     * @return 
     */
    @PostMapping("/qualifications/save")
	@ResponseBody
    public IEntity saveQualifications(@RequestBody QualificationsEntity entity) {
        return qualificationsService.saveQualifications(entity);
    }

    /**
     * IDリストの資格情報を削除する
     * @param IDS
     * @return 
     */
    @PostMapping("/qualifications/delete/id")
    @ResponseBody
    public IEntity deleteQualificationsByIds(@RequestParam int id, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        return qualificationsService.deleteQualificationsById(id, userName);
    }
}
