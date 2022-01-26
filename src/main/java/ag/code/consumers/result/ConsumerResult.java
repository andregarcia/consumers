package ag.code.consumers.result;

import ag.code.consumers.Consumer;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;


public class ConsumerResult<T, U> {

    protected Consumer<T, U> consumer;
    protected Future<U> result;

    public ConsumerResult(Consumer<T, U> consumer, Future<U> result) {
        this.consumer = consumer;
        this.result = result;
    }

    public Future<U> getResult() {
        return result;
    }

    public Consumer<T, U> getConsumer(){
        return consumer;
    }

    public U get() throws ExecutionException, InterruptedException {
        return result.get();
    }

    public U get(long timeout, TimeUnit timeUnit) throws ExecutionException, InterruptedException, TimeoutException {
        return result.get(timeout, timeUnit);
    }

}
