package sit.tuvarna.bg.api.base;

public interface Processor <R extends ProcessorResponse, I extends ProcessorRequest> {
    R process(I request);
}
