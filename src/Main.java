import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Main {

    public static void main(String[] args) throws IOException {
        testFactoryAndInterface();
        testTreeWithDouble();
        testTreeWithGps();
    }

    private static void testFactoryAndInterface() throws IOException {
        title("1. Фабрика и интерфейс UserType");

        for (TypesUserTypes type : TypesUserTypes.values()) {
            UserType t = UserFactory.getUserType(type);
            System.out.println("Фабрика(" + type + ") -> typeName() = " + t.typeName());
            System.out.println("  create() = " + t.create());
            System.out.println("  clone() = " + t.clone());
        }

        System.out.println("\nUserDouble");
        UserType dProto = UserFactory.getUserType(TypesUserTypes.DOUBLE);

        UserType d1 = (UserType) dProto.parseValue("3.14");
        System.out.println("parseValue(\"3.14\") = " + d1);
        System.out.println("parseValue(\"abc\") = " + dProto.parseValue("abc"));
        System.out.println("parseValue(null) = " + dProto.parseValue(null));

        UserType d2 = (UserType) dProto.readValue(reader("2.71\n"));
        System.out.println("readValue(\"2.71\") = " + d2);
        System.out.println("readValue(\"\") = " + dProto.readValue(reader("")));

        UserType d1copy = (UserType) d1.clone();
        System.out.println("clone: копия = " + d1copy
                + ", equals = " + d1.equals(d1copy)
                + ", тот же объект = " + (d1 == d1copy));

        Comparator dCmp = dProto.getTypeComparator();
        System.out.println("compare(3.14, 2.71) = " + sign(dCmp.compare(d1, d2)) + " (ожидаем +)");
        System.out.println("compare(2.71, 3.14) = " + sign(dCmp.compare(d2, d1)) + " (ожидаем -)");
        System.out.println("compare(3.14, 3.14) = " + sign(dCmp.compare(d1, d1copy)) + " (ожидаем 0)");

        System.out.println("\nGpscordinate");
        UserType gProto = UserFactory.getUserType(TypesUserTypes.GPSCOORDINATE);

        UserType g1 = (UserType) gProto.parseValue("55.7539;37.6208;2026-10-20T12:00:00");
        System.out.println("parseValue = " + g1);
        System.out.println("parseValue(\"1;2\")  " + gProto.parseValue("1;2"));

        UserType g2 = (UserType) gProto.readValue(reader("59.9398;30.3146;2026-10-20T14:00:00\n"));
        System.out.println("readValue = " + g2);
        System.out.println("readValue(\"\")  = " + gProto.readValue(reader("")));

        UserType g1copy = (UserType) g1.clone();
        System.out.println("clone: копия = " + g1copy
                + ", equals = " + g1.equals(g1copy)
                + ", тот же объект = " + (g1 == g1copy));

        Comparator gCmp = gProto.getTypeComparator();
        System.out.println("compare(Москва 12:00, СПб 14:00) = " + sign(gCmp.compare(g1, g2)) + " (ожидаем -)");
        System.out.println("compare(СПб 14:00, Москва 12:00) = " + sign(gCmp.compare(g2, g1)) + " (ожидаем +)");
        System.out.println("compare(Москва, копия Москвы) = " + sign(gCmp.compare(g1, g1copy)) + " (ожидаем 0)");

    }

    private static void testTreeWithDouble() {
        title("2. BinaryTree + UserDouble");
        UserType proto = UserFactory.getUserType(TypesUserTypes.DOUBLE);

        BinaryTree tree = new BinaryTree();
        System.out.println("Пустое дерево: isEmpty = " + tree.isEmpty()
                + ", size = " + tree.size() + ", height = " + tree.height());

        System.out.println("\nВставка:");
        String[] input = {"40", "20", "60", "10", "30"};
        for (String s : input) {
            UserType v = (UserType) proto.parseValue(s);
            tree.insert(v);
            System.out.println("insert(" + v + "):");
            printState(tree);
        }

        System.out.println("\nОбход forEach (in-order, по возрастанию):");
        printState(tree);

        System.out.println("\nПолучение по индексу get:");
        for (int i = 0; i < tree.size(); i++) {
            System.out.println("get(" + i + ") = " + tree.get(i));
        }

        System.out.println("\nУдаление по индексу remove:");
        System.out.println("remove(0) = " + tree.remove(0));
        printState(tree);

        UserType v45 = (UserType) proto.parseValue("45");
        tree.insert(v45);
        System.out.println("insert(" + v45 + "):");
        printState(tree);


        System.out.println("\nВыход за границы индекса:");
        try {
            tree.get(tree.size());
        } catch (IndexOutOfBoundsException e) {
            System.out.println("get(size)  -> " + e.getMessage());
        }
        try {
            tree.remove(-1);
        } catch (IndexOutOfBoundsException e) {
            System.out.println("remove(-1) -> " + e.getMessage());
        }
    }

    private static void testTreeWithGps() {
        title("3. BinaryTree + Gpscordinate (сортировка по времени)");
        UserType proto = UserFactory.getUserType(TypesUserTypes.GPSCOORDINATE);

        String[] input = {
                "56.8587;35.9176;2026-10-20T14:00:00",
                "55.7539;37.6208;2026-10-20T12:00:00",
                "59.9398;30.3146;2026-10-20T17:00:00",
                "58.5215;31.2755;2026-10-20T16:00:00",
                "57.5913;34.5645;2026-10-20T15:00:00",
        };
        BinaryTree tree = new BinaryTree();
        for (String s : input) {
            UserType v = (UserType) proto.parseValue(s);
            tree.insert(v);
            System.out.println("insert -> size = " + tree.size() + ", height = " + tree.height());
        }

        System.out.println("\nОбход forEach (по времени):");
        printList(tree);


        System.out.println("\nremove(2) = " + tree.remove(2));
        printList(tree);

        System.out.println("\nremove(0) = " + tree.remove(0));
        printList(tree);
    }


    private static void title(String s) {
        System.out.println("\n" + s);
    }

    private static InputStreamReader reader(String s) {
        return new InputStreamReader(new ByteArrayInputStream(s.getBytes(StandardCharsets.UTF_8)),
                StandardCharsets.UTF_8);
    }

    private static String sign(int x) {
        return x > 0 ? "+" : (x < 0 ? "-" : "0");
    }

    private static void printState(BinaryTree t) {
        StringBuilder sb = new StringBuilder("[");
        boolean[] first = {true};
        t.forEach(v -> {
            if (!first[0]) sb.append(", ");
            sb.append(v);
            first[0] = false;
        });
        sb.append("]");
        System.out.println("обход=" + sb + "  size=" + t.size() + "  height=" + t.height());
    }

    private static void printList(BinaryTree t) {
        int[] i = {0};
        t.forEach(v -> System.out.println("  [" + (i[0]++) + "] " + v));
        System.out.println("  size=" + t.size() + "  height=" + t.height());
    }


}