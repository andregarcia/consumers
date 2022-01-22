package ag.code.consumers;

import java.util.function.Consumer;

public interface FinalizableConsumer<T> extends Consumer<T> {

    default void finalize(){ };

}
