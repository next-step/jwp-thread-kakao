package camp.nextstep.jwpthreadkakao.thread;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import static org.assertj.core.api.Assertions.assertThat;

public class ThreadPoolsTest {
    private static final Logger log = LoggerFactory.getLogger(ThreadPoolsTest.class);

    @Test
    void newFixedThreadPoolTest() {
        final var executor = (ThreadPoolExecutor) Executors.newFixedThreadPool(2);
        executor.submit(log("fixed thread pools"));
        executor.submit(log("fixed thread pools"));
        executor.submit(log("fixed thread pools"));

        final int expectedPoolSize = 2;
        final int expectedQueueSize = 0; //TODO: 알맞은 값으로 변경해보세요.

        assertThat(expectedPoolSize).isEqualTo(executor.getPoolSize());
        assertThat(expectedQueueSize).isEqualTo(executor.getQueue().size());
    }

    @Test
    void newCachedThreadPoolTest() {
        final var executor = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        executor.submit(log("cached thread pools"));
        executor.submit(log("cached thread pools"));
        executor.submit(log("cached thread pools"));

        final int expectedPoolSize = 3;
        final int expectedQueueSize = 0; //TODO: 알맞은 값으로 변경해보세요.

        assertThat(expectedPoolSize).isEqualTo(executor.getPoolSize());
        assertThat(expectedQueueSize).isEqualTo(executor.getQueue().size());
    }

    private Runnable log(final String message) {
        return () -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            log.info(message);
        };
    }
}
