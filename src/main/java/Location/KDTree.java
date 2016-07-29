package location;


import data.Node;

public class KdTree {
    private Node element;
    private KdTree left, right;
    private int axis;
    private int size;

    public KdTree() {
    }

    public KdTree(Node element, KdTree left, KdTree right, int axis) {
        this.element = element;
        this.left = left;
        this.right = right;
        this.axis = axis;
    }

    public KdTree(KdTree oldTree) {
        if (oldTree == null) {
            this.element = null;
            this.left = null;
            this.right = null;
            this.axis = 0;
        }
        this.element = new Node(oldTree.getElement());
        this.left = oldTree.getLeft();
        this.right =oldTree.getRight();
    }

    public KdTree(Node node, int axis) {
        this.element = node;
        this.left = this.right = null;
        this.axis = axis;
    }

    public int size() {
        return size;
    }

    public void insertNode(Node node) {
        size++;
        /* empty tree */
        if (element == null) {
            element = node;
            return;
        }
        /* no children */
        if (left == null) {
            /* compare to element and shuffle */

            left = new KdTree(node, (axis + 1) % 2);
            return;
        }
    }

    public Node getElement() {
        return element;
    }

    public void setElement(Node element) {
        this.element = element;
    }

    public KdTree getLeft() {
        return new KdTree(left);
    }

    public void setLeft(KdTree left) {
        this.left = left;
    }

    public KdTree getRight() {
        return new KdTree(right);
    }

    public void setRight(KdTree right) {
        this.right = right;
    }

    public int getAxis() {
        return axis;
    }

    public void setAxis(int axis) {
        this.axis = axis;
    }
}
