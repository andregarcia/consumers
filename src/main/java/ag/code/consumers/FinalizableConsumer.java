package ag.code.consumers;


public abstract class FinalizableConsumer<T, U> extends Consumer<T, U> {

    public FinalizableConsumer() {
        super();
    }

    public void finalize(){ };

}
