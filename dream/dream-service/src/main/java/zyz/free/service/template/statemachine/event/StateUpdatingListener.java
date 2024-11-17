package zyz.free.service.template.statemachine.event;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import zyz.free.service.template.statemachine.event.handler.dto.StateDataHolder;
import zyz.free.service.template.statemachine.event.handler.impl.InitStateHandler;
import zyz.free.service.template.statemachine.event.handler.impl.ProcessingStateHandler;

import javax.annotation.Resource;

@Component
@Log4j2
public class StateUpdatingListener {

    @Resource
    private InitStateHandler initStateHandler;
    @Resource
    private ProcessingStateHandler processingStateHandler;

    @Order(1)
    @EventListener(StateUpdatingEvent.class)
    public void init(StateUpdatingEvent event) {
        StateDataHolder stateDataHolder = event.getStateDataHolder();
        if (initStateHandler.checkState(stateDataHolder.getOrderModel())) {
            initStateHandler.process(stateDataHolder);
        }
    }


    @Order(2)
    @EventListener(StateUpdatingEvent.class)
    public void processing(StateUpdatingEvent event) {
        StateDataHolder stateDataHolder = event.getStateDataHolder();
        if (processingStateHandler.checkState(stateDataHolder.getOrderModel())) {
            processingStateHandler.process(stateDataHolder);
        }
    }


}
