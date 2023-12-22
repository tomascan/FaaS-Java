public interface Action <T, R>{
    R run(T arg);
}
