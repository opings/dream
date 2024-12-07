package zyz.free.util.noc.apns;

import com.aggregator.notification.pojo.request.NotificationAppVendorRequest;
import com.aggregator.notification.pojo.response.NotificationVendorResponse;
import com.aggregator.notification.service.client.apns.config.ApnsConfig;
import com.aggregator.notification.util.NotificationTemplateUtils;
import com.eatthepath.pushy.apns.ApnsClient;
import com.eatthepath.pushy.apns.ApnsClientBuilder;
import com.eatthepath.pushy.apns.PushNotificationResponse;
import com.eatthepath.pushy.apns.auth.ApnsSigningKey;
import com.eatthepath.pushy.apns.util.SimpleApnsPayloadBuilder;
import com.eatthepath.pushy.apns.util.SimpleApnsPushNotification;
import com.eatthepath.pushy.apns.util.concurrent.PushNotificationFuture;
import com.supercode.master.monitor.annotation.Metrics;
import com.supercode.master.utils.json.JacksonUtil;
import com.supercode.monitor.aop.CatMonitor;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;

/**
 * @Author: Aaron Wu
 * @Date: 2023/11/28 09:06
 * <a href="https://github.com/jchambers/pushy"/>
 *
 * https://my.oschina.net/hejunbinlan/blog/494721
 */
@Component
@Slf4j
public class ApnsPushClient {

    @Resource
    private ApnsConfig apnsConfig;

    private ApnsClient apnsClient;

    @PostConstruct
    public void init() {
        try {
            apnsClient = new ApnsClientBuilder()
                    .setApnsServer(apnsConfig.getHostname())
                    .setSigningKey(ApnsSigningKey.loadFromInputStream(
                            new ByteArrayInputStream(apnsConfig.getPrivateKey().getBytes()), apnsConfig.getTeamId(), apnsConfig.getKeyId()
                    )).build();
        } catch (Exception e) {
            log.error("ApnsPushClient init error", e);
        }
    }

    @PreDestroy
    public void destroy() {
        if(apnsClient != null) {
            apnsClient.close();
        }
    }

    @CatMonitor
    @Metrics
    public NotificationVendorResponse push(NotificationAppVendorRequest request) {
        String payload = new SimpleApnsPayloadBuilder()
                .setAlertTitle(request.getTitle())
                .setAlertBody(request.getContent())
                .addCustomProperty(NotificationTemplateUtils.getNativeMsgExtra(), NotificationTemplateUtils.buildAppMsgExtra(request.getId().toString(), request.isGroup(), request.getAppLink()))
                .build();
        SimpleApnsPushNotification pushNotification = new SimpleApnsPushNotification(request.getToken(), apnsConfig.getTopic(), payload);
        PushNotificationFuture<SimpleApnsPushNotification, PushNotificationResponse<SimpleApnsPushNotification>> pushFuture = apnsClient.sendNotification(pushNotification);
        try {
            PushNotificationResponse<SimpleApnsPushNotification> response = pushFuture.get();
            log.info("ApnsPushClient push response: {}", JacksonUtil.toJsonStr(response));
                return new NotificationVendorResponse(request.getId())
                        .setRecordStatus(response.isAccepted())
                        .setExternalId(response.getApnsId() == null ? null : response.getApnsId().toString())
                        .setExternalStatus(String.valueOf(response.getStatusCode()))
                        .setExternalErrorReason(response.getRejectionReason().orElse(null));
        } catch (Exception e) {
            log.error("ApnsPushClient push error", e);
            return new NotificationVendorResponse(request.getId())
                    .setRecordStatus(false)
                    .setExternalErrorReason(e.getMessage());
        }
    }

}
