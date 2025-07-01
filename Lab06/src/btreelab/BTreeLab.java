
public class BTreeLab {
    public static void main(String[] args) {
        int gradoMinimo = 5;
        BTree tree = new BTree(gradoMinimo);

        int[] datos = { 10, 20, 5, 6, 12, 30, 7, 17 };

        for (int dato : datos) {
            tree.insert(dato);
        }

        System.out.println("Árbol después de inserciones:");
        tree.traverse();

        System.out.println("\n\nEliminando 6, 13 (no existe), 7:");
        tree.remove(6);
        tree.remove(13);
        tree.remove(7);

        System.out.println("Árbol después de eliminaciones:");
        tree.traverse();
    }
}
