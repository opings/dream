package zyz.free.util.noc.apns;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author: Aaron Wu
 * @Date: 2023/11/28 09:10
 */
@Component
@Getter
public class ApnsConfig {

    @Value("${notification.vendor.apns.hostname:}")
    private String hostname;
    @Value("${notification.vendor.apns.private-key:}")
    private String privateKey;
    @Value("${notification.vendor.apns.team-id:}")
    private String teamId;
    @Value("${notification.vendor.apns.key-id:}")
    private String keyId;
    @Value("${notification.vendor.apns.topic:}")
    private String topic;

}
