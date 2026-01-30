package com.kyouseipro.neo.personnel.timeworks.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.kyouseipro.neo.dto.IdRequest;
import com.kyouseipro.neo.personnel.timeworks.entity.TimeworksEntity;
import com.kyouseipro.neo.personnel.timeworks.entity.TimeworksRegistRequest;
import com.kyouseipro.neo.personnel.timeworks.service.TimeworksService;

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

    /**
     * 打刻したデータを保存する
     * @param req
     * @return
     */
    @PostMapping("/regist/today")
    public ResponseEntity<?> registToday(@RequestBody TimeworksRegistRequest req) {
        timeworksService.registToday(req.getDto(), req.getCategory());
        return ResponseEntity.ok().build();
    }

    /**
     * エラー時の処理
     * @param ex
     * @return
     */
    @ExceptionHandler({IllegalStateException.class, IllegalArgumentException.class})
    public ResponseEntity<?> handle(RuntimeException ex) {
        return ResponseEntity
            .badRequest()
            .body(ex.getMessage());
    }
}