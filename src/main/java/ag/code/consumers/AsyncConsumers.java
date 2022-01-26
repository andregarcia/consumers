package ag.code.consumers;

import ag.code.consumers.result.*;
import ag.code.consumers.result.collector.MultiConsumerResultCollector;
import ag.code.consumers.result.collector.MultiConsumerResultsCollector;

import java.util.*;
import java.util.concurrent.*;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;


public class AsyncConsumers<T, U> {

    private TreeMap<Integer, Consumer<T, U>> consumers;

    private ThreadPoolExecutor threadPoolExecutor;

    private List<Future<U>> tasks;

    public AsyncConsumers(int nThreads) {
        consumers = new TreeMap<>();
        tasks = Collections.synchronizedList(new ArrayList<>());
        threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(nThreads);
    }

    public AsyncConsumers<T, U> add(int index, Consumer<T, U> consumer) {
        consumer.setIndex(index);
        consumers.put(index, consumer);
        return this;
    }

    public AsyncConsumers<T, U> add(Consumer<T, U> consumer) {
        int index = consumer.getIndex() != null ? consumer.getIndex() : nextIndex();
        return add(index, consumer);
    }

    public AsyncConsumers<T, U> add(int index, java.util.function.Consumer<T> consumer) {
        return add(index, (T t) -> {
            consumer.accept(t);
            return null;
        });
    }

    public AsyncConsumers<T, U> add(java.util.function.Consumer<T> consumer) {
        return add(nextIndex(), consumer);
    }

    public AsyncConsumers<T, U> add(int index, Supplier<U> supplier){
        return add(index, (t) -> {
            return supplier.get();
        });
    }

    public AsyncConsumers<T, U> add(Supplier<U> supplier){
        return add(nextIndex(), supplier);
    }

    public AsyncConsumers<T, U> add(int index, Function<T, U> consumer) {
        return add(index, new Consumer<>() {
            @Override
            public U apply(T t) {
                return consumer.apply(t);
            }
        });
    }

    public AsyncConsumers<T, U> add(Function<T, U> consumer) {
        return add(nextIndex(), consumer);
    }

    public int consumerCount(){
        return consumers.size();
    }

    public Collection<Consumer<T, U>> consumers(){
        return consumers.values();
    }

    public MultiConsumerResult<T, U> consume(T t) {
        return consumers().stream()
                .map((Consumer<T, U> consumer) ->
                        new ConsumerResult<>(consumer, threadPoolExecutor.submit(() -> consumer.apply(t))))
                .collect(new MultiConsumerResultCollector<>());
    }

    public MultiConsumerResults<T, U> consume(Collection<T> collection) {
        return consume(collection.stream());
    }

    public MultiConsumerResults<T, U> consume(T[] array) {
        return consume(Arrays.stream(array));

    }

    public MultiConsumerResults<T, U> consume(Stream<T> stream) {
        return stream
                .map(this::consume)
                .collect(new MultiConsumerResultsCollector<>());
    }

    public MultiConsumerResults<T, U> consumeAndWait(Stream<T> collection){
        return consumeAndWait(collection, null);
    }

    public MultiConsumerResults<T, U> consumeAndWait(Collection<T> collection){
        return consumeAndWait(collection.stream());
    }

    public MultiConsumerResults<T, U> consumeAndWait(T[] array){
        return consumeAndWait(Arrays.stream(array));
    }

    public MultiConsumerResults<T, U> consumeAndWait(Stream<T> collection, java.util.function.Consumer<Exception> exceptionHandler) {
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

    public MultiConsumerResults<T, U> consumeAndWait(Collection<T> collection, java.util.function.Consumer<Exception> exceptionHandler) {
        return consumeAndWait(collection.stream(), exceptionHandler);
    }

    private Optional<Integer> maxIndex(){
        return consumers().stream()
                .filter(Objects::nonNull)
                .map(Consumer::getIndex)
                .max(Integer::compare);
    }

    private int nextIndex(){
        return maxIndex().orElse(0) + 1;
    }

    public void finalize() {
        consumers().forEach(consumer -> {
            if (consumer instanceof FinalizableConsumer) {
                ((FinalizableConsumer<T, U>) consumer).finalize();
            }
        });
    }

}
