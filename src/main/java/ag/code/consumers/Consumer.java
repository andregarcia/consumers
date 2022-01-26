package ag.code.consumers;


import java.util.function.Function;

public abstract class Consumer<T, U> implements Function<T, U> {

    private Integer index;

    public Consumer(){

    }

    protected void setIndex(Integer index){
        this.index = index;
    }

    public Integer getIndex() {
        return index;
    }

}
