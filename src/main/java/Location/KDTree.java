package Location;

import java.util.Collection;

/**
 * KD-Tree for neighbor searches
 */
public class KDTree<E> {
    private int k;
    private int split;
    private KDTree left, right;
    private E element;
}
