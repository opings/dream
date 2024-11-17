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
public class ProcessingStateHandler implements StateHandler {
    @Override
    public boolean checkState(OrderModel orderModel) {
        return StringUtils.equals(OrderStateEnum.PROCESSING.getCode(), orderModel.getOrderState());
    }

    @Override
    public void process(StateDataHolder stateDataHolder) {
        log.info("ProcessingStateHandler start. order:{}", stateDataHolder.getOrderModel());

        OrderModel orderModel = stateDataHolder.getOrderModel();

        if (StringUtils.equals(orderModel.getOrderSubState(), OrderSubStateEnum.BEGIN.getCode())) {
            // do something CHECK_1
            Boolean checkResponse = true;

            // 如果失败
            if (!checkResponse) {
                // 更新数据库
                OrderModel updateData = OrderModel.builder()
                        .orderId(orderModel.getOrderId())
                        .orderSubState(OrderSubStateEnum.WAITING_MANUAL_CHECK_1.getCode())
                        .build();

                DreamBeanUtils.copyProperties(updateData, orderModel);
                log.info("ProcessingStateHandler CHECK_1 FAIL. order:{}", stateDataHolder.getOrderModel());
            }
            // 如果成功
            if (checkResponse) {
                // 更新数据库
                OrderModel updateData = OrderModel.builder()
                        .orderId(orderModel.getOrderId())
                        .orderSubState(OrderSubStateEnum.CHECK_1_PASS.getCode())
                        .build();

                DreamBeanUtils.copyProperties(updateData, orderModel);
                log.info("ProcessingStateHandler CHECK_1 PASS. order:{}", stateDataHolder.getOrderModel());

            }
        }


        if (StringUtils.equals(orderModel.getOrderSubState(), OrderSubStateEnum.CHECK_1_PASS.getCode())) {
            // do something CHECK_2
            Boolean checkResponse = true;

            // 如果失败
            if (!checkResponse) {
                // 更新数据库
                OrderModel updateData = OrderModel.builder()
                        .orderId(orderModel.getOrderId())
                        .orderSubState(OrderSubStateEnum.WAITING_MANUAL_CHECK_2.getCode())
                        .build();

                DreamBeanUtils.copyProperties(updateData, orderModel);
                log.info("ProcessingStateHandler CHECK_1 FAIL. order:{}", stateDataHolder.getOrderModel());

            }
            // 如果成功
            if (checkResponse) {
                // 更新数据库
                OrderModel updateData = OrderModel.builder()
                        .orderId(orderModel.getOrderId())
                        .orderState(OrderStateEnum.SUCCEED.getCode())
                        .orderSubState(OrderSubStateEnum.BEGIN.getCode())
                        .build();

                DreamBeanUtils.copyProperties(updateData, orderModel);
                log.info("ProcessingStateHandler CHECK_1 PASS. order:{}", stateDataHolder.getOrderModel());

            }

        }


        log.info("ProcessingStateHandler end. order:{}", stateDataHolder.getOrderModel());
    }

    @Override
    public void operate(OrderOperationEnum orderOperationEnum) {

    }
}
