package sit.tuvarna.bg.base;

public interface Processor <R extends ProcessorResponse, I extends ProcessorRequest> {
    R process(I request);
}
