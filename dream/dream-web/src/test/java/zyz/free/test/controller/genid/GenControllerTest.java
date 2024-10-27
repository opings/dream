package zyz.free.test.controller.genid;


import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import zyz.free.controller.genid.GenIdController;
import zyz.free.genid.response.SimpleResponse;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@SpringBootTest
@Log4j2
public class GenControllerTest {

    @Autowired
    private GenIdController genIdController;

    @Test
    public void GenIdTest() throws ExecutionException {

        ExecutorService taskExecutor = new ThreadPoolExecutor(200, 200,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());



        List<FutureTask<Set<Long>>> list = new ArrayList<>();

        for (int i = 0; i < 200; i++) {
            FutureTask<Set<Long>> task = new FutureTask<>(() -> {
                Set<Long> idSet = new HashSet<>();
                for (int j = 0; j < 100; j++) {
                    SimpleResponse<Long> stringSimpleResponse = genIdController.GenId();
                    Long response = stringSimpleResponse.getData();
                    idSet.add(response);
                }
                System.out.println("subIdSet count:" + idSet.size());
                return idSet;
            });
            list.add(task);

            taskExecutor.execute(task);
        }


        Set<Long> totalIdSet = new HashSet<>();
        for (FutureTask<Set<Long>> futureTask : list) {
            Set<Long> set = null;
            try {
                set = futureTask.get();
                if (set.size() !=100) {
                    log.error("subIdSet count<100, subSet:" + set);
                }
            } catch (InterruptedException e) {
                log.error("subIdSet count<100, subSet:" + set);
                continue;
            }

            for (Long id : set) {
                if (!totalIdSet.add(id)) {
                    log.error("add failed id:" + id);
                }
            }

        }



        log.info(String.format("totalIdSet count:%d, taskListsize:%d", totalIdSet.size(), list.size()));


        taskExecutor.shutdown();


    }

    @Test
    public void GenIdTest1() throws ExecutionException, InterruptedException {

        ExecutorService taskExecutor = new ThreadPoolExecutor(200, 200,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>());

        long startTime = System.currentTimeMillis();

        List<FutureTask<Boolean>> list = new ArrayList<>();

        Set<Long> totalIdSet = new HashSet<>();

        for (int i = 0; i < 1000; i++) {
            FutureTask<Boolean> task = new FutureTask<>(() -> {
                try {
                    SimpleResponse<Long> stringSimpleResponse = genIdController.GenId();
                    Long response = stringSimpleResponse.getData();
                    boolean add = totalIdSet.add(response);
                    if (!add) {
                        log.error("add failed id:" + response);
                    }
                    return true;
                } catch (Exception e) {
                    log.error("genid error", e);
                    return false;
                }

            });
            list.add(task);

            taskExecutor.execute(task);
        }


        for (FutureTask<Boolean> futureTask : list) {
            if (futureTask.get()) {

            }
        }

        long endTime = System.currentTimeMillis();
        Thread.sleep(100000);
        List<Long> collect = totalIdSet.stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());

        System.out.println("totalIdSet count:" + totalIdSet.size() + ", cost:" + (endTime - startTime));
        System.out.println(collect);

        taskExecutor.shutdown();


    }


}
