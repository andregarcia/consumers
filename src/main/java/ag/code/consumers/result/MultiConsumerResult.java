package ag.code.consumers.result;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MultiConsumerResult<T, U> {

    protected List<ConsumerResult<T, U>> results;

    public MultiConsumerResult(){
        this.results = new ArrayList<>();
    }

    public MultiConsumerResult(List<ConsumerResult<T, U>> results) {
        this.results = results;
    }

    public MultiConsumerResult<T, U> add(ConsumerResult<T, U> result){
        this.results.add(result);
        return this;
    }

    public List<ConsumerResult<T, U>> getResults(){
        return Collections.unmodifiableList(results);
    }

}
