package zyz.free.service.template.statemachine.event.handler.impl;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import zyz.free.model.OrderModel;
import zyz.free.model.enums.OrderOperationEnum;
import zyz.free.model.enums.OrderStateEnum;
import zyz.free.service.template.statemachine.event.handler.StateHandler;
import zyz.free.service.template.statemachine.event.handler.dto.StateDataHolder;
import zyz.free.util.DreamBeanUtils;

@Component
@Log4j2
public class ProcessingStateHandler implements StateHandler {
    @Override
    public boolean checkState(OrderModel orderModel) {
        return StringUtils.equals(OrderStateEnum.PROCESSING.getCode(), orderModel.getOrderState());
    }

    @Override
    public void process(StateDataHolder stateDataHolder) {
        log.info("ProcessingStateHandler start orderId:{}", stateDataHolder.getOrderModel().getOrderId());

        OrderModel orderModel = stateDataHolder.getOrderModel();

        OrderModel updateData = OrderModel.builder()
                .orderId(orderModel.getOrderId())
                .orderState(OrderStateEnum.SUCCEED.getCode())
                .build();

        DreamBeanUtils.copyProperties(updateData, orderModel);

        log.info("ProcessingStateHandler end orderId:{}", stateDataHolder.getOrderModel().getOrderId());
    }

    @Override
    public void operate(OrderOperationEnum orderOperationEnum) {

    }
}
