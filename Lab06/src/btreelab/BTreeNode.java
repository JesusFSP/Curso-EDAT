
public class BTreeNode {
    int[] keys; // Claves almacenadas en el nodo
    int t; // Grado mínimo
    BTreeNode[] children;// Arreglo de hijos
    int n; // Número actual de claves
    boolean leaf; // Verdadero si es un nodo hoja

    public BTreeNode(int t, boolean leaf) {
        this.t = t;
        this.leaf = leaf;
        this.keys = new int[2 * t - 1];
        this.children = new BTreeNode[2 * t];
        this.n = 0;
    }

    // Recorrer el árbol in-order
    public void traverse() {
        int i;
        for (i = 0; i < n; i++) {
            if (!leaf)
                children[i].traverse();
            System.out.print(keys[i] + " ");
        }

        if (!leaf)
            children[i].traverse();
    }

    // Buscar una clave en el árbol
    public BTreeNode search(int key) {
        int i = 0;
        while (i < n && key > keys[i])
            i++;

        if (i < n && key == keys[i])
            return this;

        if (leaf)
            return null;

        return children[i].search(key);
    }

    // Insertar en un nodo no lleno
    public void insertNonFull(int key) {
        int i = n - 1;

        if (leaf) {
            while (i >= 0 && keys[i] > key) {
                keys[i + 1] = keys[i];
                i--;
            }
            keys[i + 1] = key;
            n++;
        } else {
            while (i >= 0 && keys[i] > key)
                i--;

            if (children[i + 1].n == 2 * t - 1) {
                splitChild(i + 1, children[i + 1]);

                if (keys[i + 1] < key)
                    i++;
            }
            children[i + 1].insertNonFull(key);
        }
    }

    // Dividir un hijo lleno
    public void splitChild(int i, BTreeNode y) {
        BTreeNode z = new BTreeNode(y.t, y.leaf);
        z.n = t - 1;

        for (int j = 0; j < t - 1; j++)
            z.keys[j] = y.keys[j + t];

        if (!y.leaf) {
            for (int j = 0; j < t; j++)
                z.children[j] = y.children[j + t];
        }

        y.n = t - 1;

        for (int j = n; j >= i + 1; j--)
            children[j + 1] = children[j];

        children[i + 1] = z;

        for (int j = n - 1; j >= i; j--)
            keys[j + 1] = keys[j];

        keys[i] = y.keys[t - 1];
        n++;
    }

    // Método para eliminar una clave del subárbol con raíz en este nodo
    public void remove(int key) {
        int idx = findKey(key);

        if (idx < n && keys[idx] == key) {
            if (leaf)
                removeFromLeaf(idx);
            else
                removeFromNonLeaf(idx);
        } else {
            if (leaf) {
                System.out.println("La clave " + key + " no existe en el árbol");
                return;
            }

            boolean flag = (idx == n);
            if (children[idx].n < t)
                fill(idx);

            if (flag && idx > n)
                children[idx - 1].remove(key);
            else
                children[idx].remove(key);
        }
    }

    private int findKey(int key) {
        int idx = 0;
        while (idx < n && keys[idx] < key)
            ++idx;
        return idx;
    }

    private void removeFromLeaf(int idx) {
        for (int i = idx + 1; i < n; ++i)
            keys[i - 1] = keys[i];
        n--;
    }

    private void removeFromNonLeaf(int idx) {
        int k = keys[idx];

        if (children[idx].n >= t) {
            int pred = getPredecessor(idx);
            keys[idx] = pred;
            children[idx].remove(pred);
        } else if (children[idx + 1].n >= t) {
            int succ = getSuccessor(idx);
            keys[idx] = succ;
            children[idx + 1].remove(succ);
        } else {
            merge(idx);
            children[idx].remove(k);
        }
    }

    private int getPredecessor(int idx) {
        BTreeNode cur = children[idx];
        while (!cur.leaf)
            cur = cur.children[cur.n];
        return cur.keys[cur.n - 1];
    }

    private int getSuccessor(int idx) {
        BTreeNode cur = children[idx + 1];
        while (!cur.leaf)
            cur = cur.children[0];
        return cur.keys[0];
    }

    private void fill(int idx) {
        if (idx != 0 && children[idx - 1].n >= t)
            borrowFromPrev(idx);
        else if (idx != n && children[idx + 1].n >= t)
            borrowFromNext(idx);
        else {
            if (idx != n)
                merge(idx);
            else
                merge(idx - 1);
        }
    }

    private void borrowFromPrev(int idx) {
        BTreeNode child = children[idx];
        BTreeNode sibling = children[idx - 1];

        for (int i = child.n - 1; i >= 0; --i)
            child.keys[i + 1] = child.keys[i];

        if (!child.leaf) {
            for (int i = child.n; i >= 0; --i)
                child.children[i + 1] = child.children[i];
        }

        child.keys[0] = keys[idx - 1];

        if (!child.leaf)
            child.children[0] = sibling.children[sibling.n];

        keys[idx - 1] = sibling.keys[sibling.n - 1];

        child.n += 1;
        sibling.n -= 1;
    }

    private void borrowFromNext(int idx) {
        BTreeNode child = children[idx];
        BTreeNode sibling = children[idx + 1];

        child.keys[child.n] = keys[idx];

        if (!child.leaf)
            child.children[child.n + 1] = sibling.children[0];

        keys[idx] = sibling.keys[0];

        for (int i = 1; i < sibling.n; ++i)
            sibling.keys[i - 1] = sibling.keys[i];

        if (!sibling.leaf) {
            for (int i = 1; i <= sibling.n; ++i)
                sibling.children[i - 1] = sibling.children[i];
        }

        child.n += 1;
        sibling.n -= 1;
    }

    private void merge(int idx) {
        BTreeNode child = children[idx];
        BTreeNode sibling = children[idx + 1];

        child.keys[t - 1] = keys[idx];

        for (int i = 0; i < sibling.n; ++i)
            child.keys[i + t] = sibling.keys[i];

        if (!child.leaf) {
            for (int i = 0; i <= sibling.n; ++i)
                child.children[i + t] = sibling.children[i];
        }

        for (int i = idx + 1; i < n; ++i)
            keys[i - 1] = keys[i];

        for (int i = idx + 2; i <= n; ++i)
            children[i - 1] = children[i];

        child.n += sibling.n + 1;
        n--;
    }

}
