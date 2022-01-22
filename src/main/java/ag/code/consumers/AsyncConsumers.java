package ag.code.consumers;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;


public class AsyncConsumers<T> {

    private List<Consumer> consumers;

    private ThreadPoolExecutor threadPoolExecutor;

    public AsyncConsumers(int nThreads) {
        consumers = new LinkedList<>();
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
    }

    public AsyncConsumers add(Consumer lyContentConsumer) {
        consumers.add(lyContentConsumer);
        return this;
    }

    public void consume(T t) {
        consumers.forEach((consumer) -> threadPoolExecutor.submit(() -> consumer.accept(t)));
    }

    public void consume(Collection<T> collection) {
        collection.forEach(t -> consume(t));
    }

    public void finalize() {
        consumers.forEach(consumer -> {
            if (consumer instanceof FinalizableConsumer) {
                ((FinalizableConsumer) consumer).finalize();
            }
        });
    }

}
