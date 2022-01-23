package ag.code.consumers.result.collector;

import ag.code.consumers.result.MultiConsumerResult;
import ag.code.consumers.result.MultiConsumerResults;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

public class MultiConsumerResultsCollector<T, U> implements Collector<MultiConsumerResult<T, U>,
        List<MultiConsumerResult<T, U>>, MultiConsumerResults<T, U>> {

    @Override
    public Supplier<List<MultiConsumerResult<T, U>>> supplier() {
        return ArrayList::new;
    }

    @Override
    public BiConsumer<List<MultiConsumerResult<T, U>>, MultiConsumerResult<T, U>> accumulator() {
        return List::add;
    }

    @Override
    public BinaryOperator<List<MultiConsumerResult<T, U>>> combiner() {
        return (l1, l2) -> {
            l1.addAll(l2);
            return l1;
        };
    }

    @Override
    public Function<List<MultiConsumerResult<T, U>>, MultiConsumerResults<T, U>> finisher() {
        return MultiConsumerResults::new;
    }

    @Override
    public Set<Characteristics> characteristics() {
        Set<Characteristics> characteristics = new HashSet<>();
        characteristics.add(Characteristics.CONCURRENT);
        return characteristics;
    }
}
