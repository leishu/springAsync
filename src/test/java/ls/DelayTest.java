package ls;

import ls.async.AsyncController;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
/**
 * Created by leishu on 17-5-23.
 */
@RunWith(SpringRunner.class)
@WebMvcTest(AsyncController.class)
public class DelayTest {
    static Logger logger = LoggerFactory.getLogger(DelayTest.class);

    @Autowired
    private MockMvc mvc;

    private void delay(String seconds) throws Exception {
        logger.info("start mock {}.", seconds);
        MvcResult mvcResult = this.mvc.perform(
                get("/delay").param("seconds", seconds))
                .andExpect(request().asyncStarted())
                .andReturn();
        this.mvc.perform(asyncDispatch(mvcResult))
                .andExpect(status().isOk())
                //.andExpect(content().contentType(MediaType.TEXT_PLAIN))
                .andExpect(content().string(seconds));

    }

    @Test
    public void delay2() throws Exception {
        delay("2");
    }

    @Test
    public void delay5() throws Exception {
        delay("5");
    }

    @Test
    public void delays() throws Exception {
        delay("5");
        delay("3");
        delay("2");
    }

    @Test
    public void concurrent() throws Exception {
        final int numThreads = 20;
        final ExecutorService threadPool = Executors.newFixedThreadPool(numThreads);
        final AtomicInteger num = new AtomicInteger();
        int count = 100;
        final CountDownLatch allDone = new CountDownLatch(100);

        int start = 0;
        while(start <= count) {
            threadPool.submit(() -> {
                try {
                    delay(Integer.toString(num.incrementAndGet() % 5 + 1));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    allDone.countDown();
                }
            });
            start++;
        }


        allDone.await();
        threadPool.shutdown();
    }
}
