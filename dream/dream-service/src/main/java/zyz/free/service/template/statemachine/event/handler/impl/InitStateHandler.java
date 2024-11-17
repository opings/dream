package zyz.free.service.template.statemachine.event.handler.impl;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import zyz.free.model.OrderModel;
import zyz.free.model.enums.OrderOperationEnum;
import zyz.free.model.enums.OrderStateEnum;
import zyz.free.model.enums.OrderSubStateEnum;
import zyz.free.service.template.statemachine.event.handler.StateHandler;
import zyz.free.service.template.statemachine.event.handler.dto.StateDataHolder;
import zyz.free.util.DreamBeanUtils;

@Component
@Log4j2
public class InitStateHandler implements StateHandler {
    @Override
    public boolean checkState(OrderModel orderModel) {
        return StringUtils.equals(OrderStateEnum.INIT.getCode(), orderModel.getOrderState());
    }

    @Override
    public void process(StateDataHolder stateDataHolder) {
        log.info("InitStateHandler start. order:{}", stateDataHolder.getOrderModel());

        OrderModel orderModel = stateDataHolder.getOrderModel();
        if (StringUtils.equals(orderModel.getOrderSubState(), OrderSubStateEnum.BEGIN.getCode())) {
            // do something


            // 更新数据库
            OrderModel updateData = OrderModel.builder()
                    .orderId(orderModel.getOrderId())
                    .orderState(OrderStateEnum.PROCESSING.getCode())
                    .orderSubState(OrderSubStateEnum.BEGIN.getCode())
                    .build();

            DreamBeanUtils.copyProperties(updateData, orderModel);
        }

        log.info("InitStateHandler end. order:{}", stateDataHolder.getOrderModel());
    }

    @Override
    public void operate(OrderOperationEnum orderOperationEnum) {

    }
}
