package ag.code.consumers.result.collector;

import ag.code.consumers.result.ConsumerResult;
import ag.code.consumers.result.MultiConsumerResult;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;


public class MultiConsumerResultCollector<T, U> implements Collector<ConsumerResult<T, U>,
        List<ConsumerResult<T, U>>, MultiConsumerResult<T, U>> {


    @Override
    public Supplier<List<ConsumerResult<T, U>>> supplier() {
        return ArrayList::new;
    }

    @Override
    public BiConsumer<List<ConsumerResult<T, U>>, ConsumerResult<T, U>> accumulator() {
        return List::add;
    }

    @Override
    public BinaryOperator<List<ConsumerResult<T, U>>> combiner() {
        return (l1, l2) -> {
            l1.addAll(l2);
            return l1;
        };
    }

    @Override
    public Function<List<ConsumerResult<T, U>>, MultiConsumerResult<T, U>> finisher() {
        return MultiConsumerResult::new;
    }

    @Override
    public Set<Characteristics> characteristics() {
        Set<Characteristics> result = new HashSet<>();
        result.add(Characteristics.CONCURRENT);
        return result;
    }
}
