public class Metric {
    private int id;
    private long executionTime;
    private int memoryUsed;


    public Metric(int id, long executionTime, int memoryUsed) {
        this.id = id;
        this.executionTime = executionTime;
        this.memoryUsed = memoryUsed;
    }

    public long getExecutionTime() {
        return executionTime;
    }

    public int getId() {
        return id;
    }

    public int getMemoryUsed() {
        return memoryUsed;
    }


    @Override
    public String toString() {
        return "Metric{" +
                "invokerId='" + id + '\'' +
                ", executionTime=" + executionTime +
                ", memoryUsed=" + memoryUsed +
                '}';
    }
}
