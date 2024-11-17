package zyz.free.test.service.template.statemachine;


import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zyz.free.service.template.statemachine.event.StateUpdatingEventPublisher;

import java.util.concurrent.*;

@SpringBootTest
@Log4j2
public class StateMachineTest {

    @Autowired
    private StateUpdatingEventPublisher stateUpdatingEventPublisher;

    @Test
    public void GenIdTest() throws ExecutionException {

        stateUpdatingEventPublisher.publish("test orderId");
    }


}
