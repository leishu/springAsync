package ls.async;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.async.DeferredResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;

/**
 * Created by leishu on 17-5-23.
 */
@Controller
public class AsyncController {
    static Logger logger = LoggerFactory.getLogger(AsyncController.class);

    @RequestMapping(value = "/delay", method= RequestMethod.GET)
    @ResponseBody
    public DeferredResult<Integer> delay(@RequestParam int seconds) {
        final DeferredResult<Integer> result = new DeferredResult<>();

        result.onTimeout(() -> {
            result.setResult(-1);
        });

        CompletableFuture<Integer> resultCompletableFuture = CompletableFuture.supplyAsync(() -> {
            try {
                logger.info("run-- {}", Thread.currentThread().getName());
                TimeUnit.SECONDS.sleep(seconds);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return seconds;
        }, new ForkJoinPool());

        resultCompletableFuture.thenAcceptAsync((value) -> {
            logger.info("return after {} seconds", value);
            result.setResult(value);
        });

        return result;
    }
}



