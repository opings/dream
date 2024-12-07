package zyz.free.util.noc.fcm;

import com.aggregator.notification.enums.NotificationRecordStatusEnum;
import com.aggregator.notification.pojo.request.NotificationAppVendorRequest;
import com.aggregator.notification.pojo.response.NotificationVendorResponse;
import com.aggregator.notification.service.client.fcm.config.FcmConfig;
import com.aggregator.notification.util.NotificationTemplateUtils;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.*;
import com.supercode.master.monitor.annotation.Metrics;
import com.supercode.monitor.aop.CatMonitor;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * @Author: Aaron Wu
 * @Date: 2023/11/26 12:32
 * api文档
 * https://developer.aliyun.com/article/1617354
 *
 * fcm 管理后台
 * https://firebase.google.cn/docs/cloud-messaging?hl=el
 */
@Component
@Slf4j
public class FcmClient {

    @Resource
    private FcmConfig fcmConfig;

    private static final String SUCCESS_STATUS = "200";

    @PostConstruct
    public void init() {
        try {
            FirebaseApp.initializeApp(
                    FirebaseOptions.builder()
                            .setCredentials(GoogleCredentials.fromStream(new ByteArrayInputStream(fcmConfig.getPrivateKey().getBytes())))
                            .build()
            );
        } catch (Exception e) {
            log.error("FcmClient init error", e);
        }
    }

    @CatMonitor
    @Metrics
    public NotificationVendorResponse push(NotificationAppVendorRequest request) {
        Message message = buildMessage(request);
        try {
            // projects/{project_id}/messages/{message_id}
            String externalId = FirebaseMessaging.getInstance().send(message);
            return buildSuccessResponse(request.getId(), externalId);
        } catch (FirebaseMessagingException e) {
            log.error("FirebaseClient push error, deviceId:{}", request.getDeviceId(), e);
            return buildFailResponse(e, request.getId());
        }
    }

    @CatMonitor
    @Metrics
    public List<NotificationVendorResponse> pushAll(List<NotificationAppVendorRequest> requests) {
        // 创建推送消息
        List<Message> list = requests.stream().map(this::buildMessage).toList();
        // 发送推送消息
        try {
            BatchResponse batchResponse = FirebaseMessaging.getInstance().sendEach(list);
            List<SendResponse> responseList = batchResponse.getResponses();
            List<NotificationVendorResponse> result = new ArrayList<>(requests.size());
            for (int i = 0; i < responseList.size(); i++) {
                SendResponse sendResponse = responseList.get(i);
                result.add(sendResponse.getException() == null
                        ? buildSuccessResponse(requests.get(i).getId(), sendResponse.getMessageId())
                        : buildFailResponse(sendResponse.getException(), requests.get(i).getId())
                );
            }
            return result;
        } catch (FirebaseMessagingException e) {
            log.error("FirebaseClient pushAll error, size: {}, msgIdList: {}", requests.size(), requests.stream().map(NotificationAppVendorRequest::getId).toList());
            return requests.stream()
                    .map(item -> buildFailResponse(e, item.getId()))
                    .toList();
        }
    }

    private Message buildMessage(NotificationAppVendorRequest request) {
        return Message.builder()
                .setNotification(Notification.builder().setTitle(request.getTitle()).setBody(request.getContent()).build())
                .setToken(request.getToken())
                .putData(NotificationTemplateUtils.getNativeMsgExtra(), NotificationTemplateUtils.buildAppMsgExtra(request.getId().toString(), request.isGroup(), request.getAppLink()))
                .build();
    }

    private NotificationVendorResponse buildFailResponse(FirebaseMessagingException e, Long msgId) {
        return new NotificationVendorResponse(msgId)
                .setStatus(NotificationRecordStatusEnum.FAIL)
                .setExternalStatus(String.valueOf(e.getHttpResponse().getStatusCode()))
                .setExternalErrorCode(e.getErrorCode().toString())
                .setExternalErrorReason(e.getMessage());
    }

    private NotificationVendorResponse buildSuccessResponse(Long msgId, String externalId) {
        return new NotificationVendorResponse(msgId)
                .setStatus(NotificationRecordStatusEnum.SUCCESS)
                .setExternalId(externalId)
                .setExternalStatus(SUCCESS_STATUS);
    }

}
