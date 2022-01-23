package ag.code.consumers;

import ag.code.consumers.result.*;
import ag.code.consumers.result.collector.MultiConsumerResultCollector;
import ag.code.consumers.result.collector.MultiConsumerResultsCollector;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;


public class AsyncConsumers<T, U> {

    private List<Consumer<T, U>> consumers;

    private ThreadPoolExecutor threadPoolExecutor;

    private List<Future<U>> tasks;

    public AsyncConsumers(int nThreads) {
        consumers = new LinkedList<>();
        tasks = Collections.synchronizedList(new ArrayList<>());
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
    }

    public AsyncConsumers<T, U> add(Consumer<T, U> lyContentConsumer) {
        consumers.add(lyContentConsumer);
        return this;
    }

    public AsyncConsumers<T, U> add(Function<T, U> consumer) {
        consumers.add(new Consumer<T, U>(consumers.size()) {
            @Override
            public U apply(T t) {
                return consumer.apply(t);
            }
        });
        return this;
    }

    public int consumers(){
        return consumers.size();
    }

    public MultiConsumerResult<T, U> consume(T t) {
        return consumers.stream()
                .map((Consumer<T, U> consumer) ->
                        new ConsumerResult<T, U>(consumer, threadPoolExecutor.submit(() -> consumer.apply(t))))
                .collect(new MultiConsumerResultCollector<T, U>());
    }

    public MultiConsumerResults<T, U> consume(Collection<T> collection) {
        return collection
                .stream()
                .map(this::consume)
                .collect(new MultiConsumerResultsCollector<T, U>());
    }

    public MultiConsumerResults<T, U> consumeAndWait(Collection<T> collection){
        return consumeAndWait(collection, null);
    }

    public MultiConsumerResults<T, U> consumeAndWait(Collection<T> collection, java.util.function.Consumer<Exception> exceptionHandler) {
        MultiConsumerResults<T, U> result = consume(collection);
        result.applyResults(c -> {
            try {
                c.get();
            } catch (InterruptedException | ExecutionException e) {
                if(exceptionHandler != null) exceptionHandler.accept(e);
                else throw new RuntimeException(e);
            }
        });
        return result;
    }


    public void finalize() {
        consumers.forEach(consumer -> {
            if (consumer instanceof FinalizableConsumer) {
                ((FinalizableConsumer<T, U>) consumer).finalize();
            }
        });
    }

}
