package ag.code.consumers;

import ag.code.consumers.result.MultiConsumerResult;
import ag.code.consumers.result.MultiConsumerResults;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AsyncConsumersTest {

    @Test
    void add() {
        AsyncConsumers<String, String> asyncConsumers = new AsyncConsumers<>(2);
        asyncConsumers
                .add((String s) -> s.toLowerCase())
                .add((String s) -> s.toUpperCase());

        assertEquals(2, asyncConsumers.consumerCount());
    }

    @Test
    void consumeOne() throws ExecutionException, InterruptedException {
        AsyncConsumers<String, String> asyncConsumers = new AsyncConsumers<>(2);
        asyncConsumers
                .add((String s) -> s.toLowerCase())
                .add((String s) -> s.toUpperCase());
        MultiConsumerResult<String, String> result = asyncConsumers.consume("AbC");

        assertEquals("abc", result.getResults().get(0).get());
        assertEquals("ABC", result.getResults().get(1).get());
    }

    @Test
    void consumeMany() throws ExecutionException, InterruptedException {
        AsyncConsumers<String, String> asyncConsumers = new AsyncConsumers<>(2);
        asyncConsumers
                .add((String s) -> s.toLowerCase())
                .add((String s) -> s.toUpperCase());
        List<String> consumable = new ArrayList<>();
        consumable.add("Abc");
        consumable.add("dEf");
        consumable.add("ghI");
        MultiConsumerResults<String, String> result = asyncConsumers.consume(consumable);

        assertEquals("abc", result.get(0).get(0).get());
        assertEquals("def", result.get(0).get(1).get());
        assertEquals("ghi", result.get(0).get(2).get());
        assertEquals("ABC", result.get(1).get(0).get());
        assertEquals("DEF", result.get(1).get(1).get());
        assertEquals("GHI", result.get(1).get(2).get());

    }

    @Test
    void consumeAndWait() throws ExecutionException, InterruptedException {
        AsyncConsumers<String, String> asyncConsumers = new AsyncConsumers<>(2);
        asyncConsumers
                .add((String s) -> {
                    try {
                        Thread.sleep(5000);
                        return s.toLowerCase();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return null;
                    }
                })
                .add((String s) -> {
                    try {
                        Thread.sleep(5000);
                        return s.toUpperCase();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        return null;
                    }
                });
        List<String> consumable = new ArrayList<>();
        consumable.add("Abc");
        consumable.add("dEf");
        long startTime = System.currentTimeMillis();
        MultiConsumerResults<String, String> result = asyncConsumers.consumeAndWait(consumable);
        long endTime = System.currentTimeMillis();

        assertTrue((endTime - startTime) > 5000);
        assertEquals("abc", result.get(0).get(0).get());
        assertEquals("def", result.get(0).get(1).get());
        assertEquals("ABC", result.get(1).get(0).get());
        assertEquals("DEF", result.get(1).get(1).get());
    }

    @Test
    public void testConsumerIndexIsSet() throws ExecutionException, InterruptedException {
        AsyncConsumers<Void, String> asyncConsumers = new AsyncConsumers<>(2);
        asyncConsumers
                .add(() -> "x")
                .add(4, new Supplier<String>() {
                    @Override
                    public String get() {
                        return "z";
                    }
                })
                .add(2, new Consumer<>() {
                    @Override
                    public String apply(Void unused) {
                        return "y";
                    }
                });

        MultiConsumerResults<Void, String> results = asyncConsumers.consumeAndWait(new Void[2]);
        assertEquals("x", results.get(0).get(0).get());
        assertEquals("x", results.get(0).get(1).get());
        assertEquals("y", results.get(1).get(0).get());
        assertEquals("y", results.get(1).get(1).get());
        assertEquals("z", results.get(2).get(0).get());
        assertEquals("z", results.get(2).get(1).get());
    }

}