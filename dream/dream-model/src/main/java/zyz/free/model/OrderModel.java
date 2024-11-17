package zyz.free.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zyz.free.model.enums.OrderStateEnum;
import zyz.free.model.enums.OrderSubStateEnum;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class OrderModel {

    private String orderId;

    /**
     * {@link OrderStateEnum}
     */
    private String orderState;

    /**
     * {@link OrderSubStateEnum}
     */
    private String orderSubState;
}
