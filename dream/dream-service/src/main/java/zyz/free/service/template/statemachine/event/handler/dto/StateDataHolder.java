package zyz.free.service.template.statemachine.event.handler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import zyz.free.model.OrderModel;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StateDataHolder {

    private OrderModel orderModel;


}
