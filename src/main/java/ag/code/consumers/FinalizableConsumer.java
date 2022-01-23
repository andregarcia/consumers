package ag.code.consumers;


public abstract class FinalizableConsumer<T, U> extends Consumer<T, U> {

    public FinalizableConsumer(int index) {
        super(index);
    }

    public void finalize(){ };

}
