package camp.nextstep.jwpthreadkakao.thread;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

public class ThreadTest {
    private static final Logger log = LoggerFactory.getLogger(ThreadTest.class);

    @DisplayName("비동기 작업 시작할때마다 Thread를 인스턴스화")
    @Test
    void createThread() {
        Thread thread = new ManualThread("hello thread");

        thread.start();
    }

    private static final class ManualThread extends Thread {
        private final String message;

        public ManualThread(final String message) {
            this.message = message;
        }

        @Override
        public void run() {
            log.info(message);
        }
    }

    @DisplayName("Task를 Executor에 전달")
    @Test
    void createThreadByExecutor() {
        Executor executor = new ThreadPerTaskExecutor();

        executor.execute(() -> log.info("hello executor"));
    }

    static class ThreadPerTaskExecutor implements Executor {
        public void execute(Runnable command) {
            new Thread(command).start();
        }
    }

    //TODO: 동기화를 이용해서 쓰레드 간섭을 해결해보세요.
    @Test
    void synchronizedTest() throws InterruptedException {
        var executorService = Executors.newFixedThreadPool(3);
        var counter = new Counter();

        IntStream.range(0, 1_000).forEach(count -> executorService.submit(counter::calculate));

        executorService.awaitTermination(500, TimeUnit.MILLISECONDS);

        assertThat(counter.getInstanceValue()).isEqualTo(1_000);
    }

    private static final class Counter {

        private int instanceValue = 0;

        public void calculate() {
            setInstanceValue(getInstanceValue() + 1);
        }

        public int getInstanceValue() {
            return instanceValue;
        }

        public void setInstanceValue(int instanceValue) {
            this.instanceValue = instanceValue;
        }
    }

    //TODO: synchronized 예약어를 사용하지 않고 Thread safe하게 구성해보세요
    @Test
    void synchronizedTest2() throws InterruptedException {
        var executorService = Executors.newFixedThreadPool(3);
        var servlet = new TestServlet();

        IntStream.range(0, 1_000).forEach(count -> executorService.submit(() -> servlet.join(new User("cu"))));


        executorService.awaitTermination(1_000, TimeUnit.MILLISECONDS);
        assertThat(servlet.getUsers().size()).isEqualTo(1);
    }

    private static final class TestServlet {
        private static final Long LATENCY = 200L;

        private List<User> users = new ArrayList<>();

        public void join(User user) {
            if (!users.contains(user)) {
                sleep();
                users.add(user);
            }
        }

        private static void sleep() {
            if (condition()) {
                try {
                    Thread.sleep(LATENCY);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        private static boolean condition() {
            return (long) (Math.random() * 10 % 2) == 0;
        }

        public List<User> getUsers() {
            return users;
        }
    }

    private class User {
        private final String name;

        private User(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            User user = (User) o;
            return Objects.equals(name, user.name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(name);
        }

        @Override
        public String toString() {
            return "User{" +
                    "name='" + name + '\'' +
                    '}';
        }
    }
}
