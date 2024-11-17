package zyz.free.service.template.statemachine.event;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;
import zyz.free.model.OrderModel;
import zyz.free.service.template.statemachine.event.handler.dto.StateDataHolder;

@Setter
@Getter
public class StateUpdatingEvent extends ApplicationEvent {


    private StateDataHolder stateDataHolder;


    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public StateUpdatingEvent(Object source, StateDataHolder stateDataHolder) {
        super(source);
        this.stateDataHolder = stateDataHolder;
    }
}
