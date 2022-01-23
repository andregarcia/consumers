package ag.code.consumers.result;

import ag.code.consumers.Consumer;

import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toList;


public class MultiConsumerResults<T, U> {

    private SortedMap<Consumer<T, U>, List<Future<U>>> results;

    public MultiConsumerResults(){
        results = newMap();
    }

    private SortedMap<Consumer<T, U>, List<Future<U>>> newMap(){
        return Collections.synchronizedSortedMap(
                new TreeMap<Consumer<T, U>, List<Future<U>>>(Comparator.comparingInt(Consumer::getIndex)));
    }

    public MultiConsumerResults(List<MultiConsumerResult<T, U>> multiConsumerResults) {
        results = multiConsumerResults
                .stream()
                .flatMap(m -> m.results.stream())
                .collect(Collectors.groupingBy(
                        ConsumerResult::getConsumer,
                        this::newMap,
                        mapping(ConsumerResult::getResult, toList())));
    }

    public void add(ConsumerResult<T, U> consumerResult){
        List<Future<U>> l = results.getOrDefault(consumerResult.consumer, new ArrayList<>());
        l.add(consumerResult.result);
    }

    public void add(MultiConsumerResult<T, U> consumersResult){
        consumersResult.results.forEach(this::add);
    }

    public List<Future<U>> get(Consumer<T, U> consumer){
        return results.get(consumer);
    }

    public List<Future<U>> get(int index){
        int i = 0;
        Iterator<List<Future<U>>> iterator = results.values().iterator();
        while(i < results.size() && iterator.hasNext()) {
            List<Future<U>> l = iterator.next();
            if(i == index) return l;
            i++;
        }
        return null;
    }

    public List<Future<U>> flattenResults(){
        return results.values().stream().flatMap(Collection::stream).collect(toList());
    }

    public void applyResults(java.util.function.Consumer<Future<U>> consumer){
        results.values().stream().flatMap(Collection::stream).forEach(consumer);
    }


}
