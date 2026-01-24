package com.kyouseipro.neo.controller.api.personnel;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kyouseipro.neo.entity.dto.IdRequest;
import com.kyouseipro.neo.entity.dto.TimeworksRegistRequest;
import com.kyouseipro.neo.entity.personnel.TimeworksEntity;
import com.kyouseipro.neo.service.personnel.TimeworksService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/timeworks")
public class TimeworksController {

    private final TimeworksService timeworksService;

    /**
     * 当日のListを取得する
     * @return
     */
    @GetMapping("/get/today")
	@ResponseBody
    public List<TimeworksEntity> getTodaysList() {
        return timeworksService.getTodaysList();
    }

    /**
     * IDで指定した従業員の当日の勤怠情報を取得する
     * @return
     */
    @PostMapping("/get/today/id")
	@ResponseBody
    public ResponseEntity<TimeworksEntity> getTodaysEntity(@RequestBody IdRequest req) {
        return ResponseEntity.ok(timeworksService.getTodaysEntity(req.getId()).orElse(null));
    }

    @PostMapping("/regist/today")
    public ResponseEntity<?> registToday(@RequestBody TimeworksRegistRequest req) {
        timeworksService.registToday(req.getDto(), req.getCategory());
        return ResponseEntity.ok().build();
    }

    // /** 出勤 */
    // @PostMapping("/start")
    // public ResponseEntity<?> start(@RequestBody TimeworksRequestDto dto) {

    //     if (dto.getStartDt() == null) {
    //         return ResponseEntity.badRequest().body("startDt is required");
    //     }

    //         timeworksService.startWork(dto);

    //     return ResponseEntity.ok().build();
    // }

    // /** 退勤 */
    // @PostMapping("/end")
    // public ResponseEntity<?> end(@RequestBody TimeworksRequestDto dto) {

    //     if (dto.getEndDt() == null) {
    //         return ResponseEntity.badRequest().body("endDt is required");
    //     }

    //     timeworksService.endWork(dto);

    //     return ResponseEntity.ok().build();
    // }

    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<?> handle(RuntimeException ex) {
        return ResponseEntity
            .badRequest()
            .body(ex.getMessage());
    }
}