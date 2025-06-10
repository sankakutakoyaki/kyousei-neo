package com.kyouseipro.neo.service.document;

import nl.martijndwars.webpush.Notification;
import nl.martijndwars.webpush.PushService;

import org.apache.http.HttpResponse;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.kyouseipro.neo.entity.data.SubscriptionRequest;
import com.kyouseipro.neo.repository.document.PushRepository;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import java.security.Security;

@Service
@RequiredArgsConstructor
public class WebPushService {
    private final PushRepository pushRepository;

    @Value("${vapid.publicKey}")
    private String publicKey;

    @Value("${vapid.privateKey}")
    private String privateKey;

    @Value("${vapid.subject}")
    private String subject;

    @PostConstruct
    public void setup() {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * メッセージを送信する
     * @param subscription
     * @param message
     * @throws Exception
     */
    public void sendPushNotification(SubscriptionRequest subscription, String message, String editor) throws Exception {
        Notification notification = new Notification(
            subscription.getEndpoint(),
            subscription.getP256dh(),
            subscription.getAuth(),
            message.getBytes()
        );

        PushService pushService = new PushService();
        pushService.setPublicKey(publicKey);
        pushService.setPrivateKey(privateKey);
        pushService.setSubject(subject);

        HttpResponse response = pushService.send(notification);

        int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != 201) {
            throw new RuntimeException("Push通知の送信に失敗しました。status=" + statusCode);
        }
        if (statusCode == 410 || statusCode == 404) {
            // 無効な subscription を削除
            pushRepository.deleteByEndpoint(subscription.getEndpoint(), editor);
            System.out.println("無効なエンドポイントだったため、削除しました。");
        }
    }
}