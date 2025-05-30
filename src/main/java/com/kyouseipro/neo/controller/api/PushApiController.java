package com.kyouseipro.neo.controller.api;

import org.springframework.web.bind.annotation.*;

import com.kyouseipro.neo.entity.data.SubscriptionRequest;
import com.kyouseipro.neo.interfaceis.Entity;
import com.kyouseipro.neo.repository.PushRepository;
import com.kyouseipro.neo.service.WebPushService;

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
    
    /**
     * JWT署名を作成
     * @param message
     * @param csrftoken
     * @return
     * @throws Exception
     */
    @PostMapping("/send")
    public void sendPush(@RequestParam String message, @RequestParam String csrftoken) throws Exception {

        Security.addProvider(new BouncyCastleProvider());
        List<Entity> list = pushRepository.getList();
        for (Entity entity : list) {
            SubscriptionRequest subscriptionRequest = (SubscriptionRequest)entity;

            try {
                webPushService.sendPushNotification(subscriptionRequest, message);
                System.out.println("Push通知を送信しました: " + subscriptionRequest.getUsername());
            } catch (Exception e) {
                e.printStackTrace();
                System.out.println("送信失敗: " + e.getMessage());
            }
        }
    }

    /**
     * クライアントから Pushサブスクリプション情報を受け取る
     * @param subscriptionRequest
     */
    @PostMapping("/subscribe")
    public boolean subscribe(@RequestBody SubscriptionRequest subscriptionRequest) {
        SubscriptionRequest result = pushRepository.findByEndpoint(subscriptionRequest.getEndpoint());
        if (result == null) {
            pushRepository.save(subscriptionRequest);
            System.out.println("Push scribe!");
            return true;
        } else {
            System.out.println("scribe failed");
            return false;
        }
    }

    /**
     * VAPID公開鍵をクライアントへ渡す
     * @return
     */
    @GetMapping("/vapid-public-key")
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
    @PostMapping("/remove-subscription")
    public void removeSubscription(@RequestParam String endpoint) {
        try {
            // データベースからエンドポイント情報を削除する
            boolean result = pushRepository.deleteByEndpoint(endpoint);
            if (result) {
                System.out.println("endpoint '" + endpoint + "'を削除しました");
            } else {
                System.out.println("endpoint '" + endpoint + "'を削除できませんでした");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
