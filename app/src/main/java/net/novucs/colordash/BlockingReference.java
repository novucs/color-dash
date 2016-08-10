package net.novucs.colordash;

/**
 * Allows safe asynchronous passing of immutable variables.
 * @param <T>
 */
public class BlockingReference<T> {

    private T reference;

    /**
     * Waits until a valid reference is set, then returns and removes it.
     * @return the next valid reference.
     * @throws InterruptedException
     */
    public T retrieve() throws InterruptedException {
        if (reference == null) {
            wait();
        }

        T target = reference;
        reference = null;
        return target;
    }

    /**
     * Updates the reference.
     * @param reference the new reference.
     */
    public void update(T reference) {
        this.reference = reference;
        notify();
    }
}
