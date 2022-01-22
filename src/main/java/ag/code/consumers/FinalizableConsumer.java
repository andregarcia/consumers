package ag.code.consumers;

import java.util.function.Consumer;

public interface FinalizableConsumer extends Consumer {

    default void finalize(){ };

}
