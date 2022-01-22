package ag.code.consumers;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Consumer;


public class AsyncConsumers<T> {

    private List<Consumer> consumers;

    private ThreadPoolExecutor threadPoolExecutor;

    private List<Future> tasks;

    private Consumer<Exception> exceptionHandler;

    public AsyncConsumers(int nThreads) {
        consumers = new LinkedList<>();
        tasks = Collections.synchronizedList(new ArrayList<>());
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
    }

    public AsyncConsumers add(Consumer lyContentConsumer) {
        consumers.add(lyContentConsumer);
        return this;
    }

    public void consume(T t) {
        consumers.forEach((consumer) -> {
            Future<?> task = threadPoolExecutor.submit(() -> consumer.accept(t));
            tasks.add(task);
        });
    }

    public void consume(Collection<T> collection) {
        collection.forEach(this::consume);
    }

    public void join(long timeout, TimeUnit timeUnit) throws ExecutionException,
            InterruptedException, TimeoutException {
        for (Future<?> f : tasks) {
            try {
                f.get(timeout, timeUnit);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                if (exceptionHandler != null) exceptionHandler.accept(e);
                else throw e;
            }
        }
    }

    public void finalize() {
        consumers.forEach(consumer -> {
            if (consumer instanceof FinalizableConsumer) {
                ((FinalizableConsumer) consumer).finalize();
            }
        });
    }

}
