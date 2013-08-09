/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package elr.core.interfaces;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

/**
 *
 * @author Infernage
 */
public interface Job<T, V> {
    public void addJob(T job);
    public void addListJobs(Collection<T> collection);
    public List<Future<V>> startJob() throws InterruptedException;
}
