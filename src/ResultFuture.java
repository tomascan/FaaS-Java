import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class ResultFuture<T> {
    private final Future<T> future;

    public ResultFuture(Future<T> future) {
        this.future = future;
    }

    public T get() throws InterruptedException, ExecutionException {
        return future.get();
    }
}