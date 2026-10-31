package com.kyouseipro.neo.common.mail;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(properties = {
    "spring.config.additional-location=file:/Users/makoto/config/"
})
@ActiveProfiles("dev")
class MailServiceTest {

    @Autowired
    private MailService mailService;

    @Test
    @Disabled("実際にメールを送信するため、通常のテストでは実行しない")
    void sendTestMail() {
        mailService.send(
            "hashimoto@kyouseibin.com",
            "【kyousei-neo】メール送信テスト",
            "kyousei-neoからのテストメールです。\n正常に受信できています。"
        );
    }
}