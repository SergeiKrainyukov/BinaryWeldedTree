import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

class BSTNode {
    public int NodeKey;
    public BSTNode Parent;
    public BSTNode LeftChild;
    public BSTNode RightChild;
    public int color;

    public BSTNode(int key, BSTNode parent, int color) {
        NodeKey = key;
        Parent = parent;
        LeftChild = null;
        RightChild = null;
        this.color = color;
    }
}

// промежуточный результат поиска
class BSTFind {
    // null если в дереве вообще нет узлов
    public BSTNode Node;

    // true если узел найден
    public boolean NodeHasKey;

    // true, если родительскому узлу надо добавить новый левым
    public boolean ToLeft;

    public BSTFind() {
        Node = null;
    }
}

class BST {
    BSTNode Root;
    int deep;
    int[] colors;

    public BST(BSTNode node, int deep, int colorsQuantity) {
        Root = node;
        this.deep = deep;
        this.colors = calculateColors(colorsQuantity);
    }

    private int[] calculateColors(int quantity) {
        int[] colors = new int[quantity];
        for (int i = 0; i < colors.length; i++) {
            colors[i] = i;
        }
        return colors;
    }

    public BSTFind FindNodeByKey(int key) {
        if (Root == null) {
            return new BSTFind();
        }
        return findNode(key, Root);
    }

    private BSTFind findNode(int key, BSTNode currentNode) {
        if (key == currentNode.NodeKey) {
            BSTFind bstFind = new BSTFind();
            bstFind.Node = currentNode;
            bstFind.NodeHasKey = true;
            return bstFind;
        }
        if (key < currentNode.NodeKey && currentNode.LeftChild != null) {
            currentNode = currentNode.LeftChild;
            return findNode(key, currentNode);
        }
        if (key > currentNode.NodeKey && currentNode.RightChild != null) {
            currentNode = currentNode.RightChild;
            return findNode(key, currentNode);
        }
        if (key < currentNode.NodeKey) {
            BSTFind bstFind = new BSTFind();
            bstFind.Node = currentNode;
            bstFind.NodeHasKey = false;
            bstFind.ToLeft = true;
            return bstFind;
        }
        BSTFind bstFind = new BSTFind();
        bstFind.Node = currentNode;
        bstFind.NodeHasKey = false;
        bstFind.ToLeft = false;
        return bstFind;
    }

    public boolean AddKeyValue(int key) {
        if (Root == null) {
            Root = new BSTNode(key, null, colors[0]);
            return true;
        }
        BSTFind bstFind = findNode(key, Root);
        if (bstFind.NodeHasKey) {
            return false;
        }
        if (bstFind.ToLeft) {
            bstFind.Node.LeftChild = new BSTNode(key, bstFind.Node, calculateColor(bstFind.Node, null));
        } else {
            bstFind.Node.RightChild = new BSTNode(key, bstFind.Node, calculateColor(bstFind.Node, bstFind.Node.LeftChild));
        }
        return true;
    }

    private int calculateColor(BSTNode parentNode, BSTNode leftChild) {
        int parentColor = parentNode.color;
        int leftChildColor = leftChild == null ? -1 : leftChild.color;
        for (int color : colors) {
            if (color != parentColor && color != leftChildColor) {
                return color;
            }
        }
        return 0;
    }

    public BSTNode FindMinMax(BSTNode FromNode, boolean FindMax) {
        if (Root == null) return null;
        if (FindMax) {
            return findMax(FromNode);
        }
        return findMin(FromNode);
    }

    private BSTNode findMin(BSTNode FromNode) {
        BSTNode nextNode = FromNode.LeftChild;
        if (nextNode == null) {
            return FromNode;
        }
        return findMin(nextNode);
    }

    private BSTNode findMax(BSTNode FromNode) {
        BSTNode nextNode = FromNode.RightChild;
        if (nextNode == null) {
            return FromNode;
        }
        return findMax(nextNode);
    }

    public boolean DeleteNodeByKey(int key) {
        if (Root == null) return false;
        BSTFind nodeForDelete = FindNodeByKey(key);
        if (key == Root.NodeKey || nodeForDelete.Node.Parent == null) {
            Root = null;
            return true;
        }
        if (nodeForDelete.Node.LeftChild == null && nodeForDelete.Node.RightChild == null) {
            removeLastNode(nodeForDelete.Node);
            return true;
        }
        BSTNode nodeForReplace;
        if (nodeForDelete.Node.RightChild == null) {
            nodeForReplace = nodeForDelete.Node.LeftChild;
        } else {
            nodeForReplace = FindMinMax(nodeForDelete.Node.RightChild, false);
        }
        if (!nodeForDelete.NodeHasKey)
            return false;
        nodeForDelete.Node.NodeKey = nodeForReplace.NodeKey;
        if (nodeForReplace.RightChild != null) {
            nodeForDelete.Node.RightChild = nodeForReplace.RightChild;
            return true;
        }
        removeLastNode(nodeForReplace);
        return true;
    }

    private void removeLastNode(BSTNode nodeForDelete) {
        if (nodeForDelete.Parent == null) return;
        if (nodeForDelete.Parent.LeftChild != null && nodeForDelete.Parent.LeftChild.equals(nodeForDelete)) {
            nodeForDelete.Parent.LeftChild = null;
        }
        if (nodeForDelete.Parent.RightChild != null && nodeForDelete.Parent.RightChild.equals(nodeForDelete)) {
            nodeForDelete.Parent.RightChild = null;
        }
    }

    public int Count() {
        if (Root == null) return 0;
        return getNodes(new ArrayList<>(), Root).size();
    }

    public ArrayList<BSTNode> WideAllNodes() {
        if (Root == null) return new ArrayList<>();
        ArrayList<BSTNode> nodeArrayList = new ArrayList<>();
        LinkedList<BSTNode> queue = new LinkedList<>();
        queue.add(Root);
        while (queue.size() > 0) {
            var tempNode = queue.get(0);
            queue.remove(0);
            nodeArrayList.add(tempNode);
            if (tempNode.LeftChild != null) {
                queue.add(tempNode.LeftChild);
            }
            if (tempNode.RightChild != null) {
                queue.add(tempNode.RightChild);
            }
        }
        return nodeArrayList;
    }

    public ArrayList<BSTNode> DeepAllNodes(int parameter) {
        ArrayList<BSTNode> nodes = new ArrayList<>();
        switch (parameter) {
            case 0 -> {
                return inOrder(nodes, Root);
            }
            case 1 -> {
                return postOrder(nodes, Root);
            }
            case 2 -> {
                return preOrder(nodes, Root);
            }
        }
        return null;
    }

    private ArrayList<BSTNode> preOrder(ArrayList<BSTNode> list, BSTNode currentNode) {
        if (currentNode == null) return list;
        list.add(currentNode);
        preOrder(list, currentNode.LeftChild);
        preOrder(list, currentNode.RightChild);
        return list;
    }

    private ArrayList<BSTNode> inOrder(ArrayList<BSTNode> list, BSTNode currentNode) {
        if (currentNode == null) return list;
        inOrder(list, currentNode.LeftChild);
        list.add(currentNode);
        inOrder(list, currentNode.RightChild);
        return list;
    }

    private ArrayList<BSTNode> postOrder(ArrayList<BSTNode> list, BSTNode currentNode) {
        if (currentNode == null) return list;
        postOrder(list, currentNode.LeftChild);
        postOrder(list, currentNode.RightChild);
        list.add(currentNode);
        return list;
    }

    public List<BSTNode> getNodes(List<BSTNode> nodeList, BSTNode currentNode) {
        nodeList.add(currentNode);
        if (currentNode.LeftChild == null && currentNode.RightChild == null) {
            return nodeList;
        }
        if (currentNode.LeftChild != null) {
            getNodes(nodeList, currentNode.LeftChild);
        }
        if (currentNode.RightChild != null) {
            getNodes(nodeList, currentNode.RightChild);
        }
        return nodeList;
    }

}
