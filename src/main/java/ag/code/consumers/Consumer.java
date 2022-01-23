package ag.code.consumers;


import java.util.function.Function;

public abstract class Consumer<T, U> implements Function<T, U> {

    private int index;

    public Consumer(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

}
