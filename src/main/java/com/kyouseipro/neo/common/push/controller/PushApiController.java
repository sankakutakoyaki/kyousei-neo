package com.kyouseipro.neo.common.push.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.*;

import com.kyouseipro.neo.common.history.service.HistoryService;
import com.kyouseipro.neo.common.push.entity.SubscriptionRequest;
import com.kyouseipro.neo.common.push.repository.PushRepository;
import com.kyouseipro.neo.common.push.service.WebPushService;
import com.kyouseipro.neo.common.response.SimpleResponse;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import lombok.RequiredArgsConstructor;

import java.security.Security;
import java.util.List;
import java.util.ResourceBundle;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/push")
public class PushApiController {

    private final PushRepository pushRepository;
    private final WebPushService webPushService;
    private final HistoryService historyService;
    
    /**
     * JWT署名を作成
     * @param message
     * @param csrftoken
     * @return
     * @throws Exception
     */
    @PostMapping("/api/send")
    public void sendPush(@RequestParam String message, @RequestParam String csrftoken, @AuthenticationPrincipal OidcUser principal) throws Exception {
        String userName = principal.getAttribute("preferred_username");
        Security.addProvider(new BouncyCastleProvider());
        List<SubscriptionRequest> list = pushRepository.findAll();
        for (SubscriptionRequest entity : list) {
            SubscriptionRequest subscriptionRequest = (SubscriptionRequest)entity;

            try {
                webPushService.sendPushNotification(subscriptionRequest, message, userName);
                System.out.println("Push通知を送信しました: " + subscriptionRequest.getUsername());
                historyService.save(userName, "push", "通知", 200, "成功");
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("送信失敗: " + e.getMessage());
                historyService.save(userName, "push", "通知", 400, "失敗");
            }
        }
    }

    /**
     * クライアントから Pushサブスクリプション情報を受け取る
     * @param subscriptionRequest
     */

    @PostMapping("/api/subscribe")
    public ResponseEntity<SimpleResponse<Boolean>> subscribe(
            @RequestBody SubscriptionRequest request,
            @AuthenticationPrincipal OidcUser principal
    ) {

        boolean exists =
                pushRepository.findByEndpoint(request.getEndpoint()) != null;
        return ResponseEntity.ok(
                SimpleResponse.ok("保存しました", exists)
        );
    }

    /**
     * VAPID公開鍵をクライアントへ渡す
     * @return
     */
    @GetMapping("/api/vapid-public-key")
    public String getVapidPublicKey() {
        try {
            return ResourceBundle.getBundle("application").getString("vapid.publicKey"); // VAPID 秘密鍵
        } catch (Exception e) {
            return "Error retrieving VAPID public key: ";
        }
    }

    /**
     * VAPID公開鍵を削除
     * @param endpoint
     */
    @PostMapping("/api/remove-subscription")
    public void removeSubscription(@RequestParam String endpoint, @AuthenticationPrincipal OidcUser principal) {
        String userName = principal.getAttribute("preferred_username");
        try {
            // データベースからエンドポイント情報を削除する
            Integer result = pushRepository.deleteByEndpoint(endpoint, userName);
            if (result > 0) {
                System.out.println("endpoint '" + endpoint + "'を削除しました");
                historyService.save(userName, "endpoint", "削除", 200, "成功");
            } else {
                System.out.println("endpoint '" + endpoint + "'を削除できませんでした");
                historyService.save(userName, "endpoint", "削除", 400, "失敗");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
