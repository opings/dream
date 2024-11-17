package zyz.free.service.template.statemachine.event.handler;

import zyz.free.model.OrderModel;
import zyz.free.model.enums.OrderOperationEnum;
import zyz.free.service.template.statemachine.event.handler.dto.StateDataHolder;

public interface StateHandler {


    /**
     * 只处理当前的类型的事件
     */
    boolean checkState(OrderModel orderModel);


    void process(StateDataHolder stateDataHolder);


    void operate(OrderOperationEnum orderOperationEnum);

}
