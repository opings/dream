package zyz.free.service.template.statemachine.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import zyz.free.model.OrderModel;
import zyz.free.model.enums.OrderStateEnum;
import zyz.free.service.template.statemachine.event.handler.dto.StateDataHolder;

import javax.annotation.Resource;

@Component
public class StateUpdatingEventPublisher {

    @Resource
    private ApplicationEventPublisher applicationEventPublisher;

    public void publish(String orderId) {
        // 加订单维度的分布式锁

        OrderModel orderModel = OrderModel.builder()
                .orderId(orderId)
                .orderState(OrderStateEnum.INIT.getCode())
                .build();

        StateDataHolder stateDataHolder = StateDataHolder.builder()
                .orderModel(orderModel)
                .build();

        StateUpdatingEvent event = new StateUpdatingEvent(applicationEventPublisher, stateDataHolder);
        applicationEventPublisher.publishEvent(event);
    }


}
