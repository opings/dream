package zyz.free.util.noc.fcm;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * @Author: Aaron Wu
 * @Date: 2023/11/28 09:06
 */
@Component
@Getter
public class FcmConfig {

    @Value("${notification.vendor.fcm.private-key:}")
    private String privateKey;

}
