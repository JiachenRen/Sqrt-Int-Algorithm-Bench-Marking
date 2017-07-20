import java.util.ArrayList;
import java.util.Scanner;

/*
Bench marking of 6 different sqrt algorithms by Jiachen Ren.
 */
public class Main {

    public static void main(String[] args) {
        System.out.print("Checking method validity...");
        int definitive;
        for (int i = 0; i < 1000000; i++) {
            definitive = (int) Math.sqrt(i);
            if (sqrtInt(i) == definitive
                    && sqrtIntNative(i) == definitive
                    && isqrt(i) == definitive
                    && floorSqrt(i) == definitive
                    && isqrt32(i) == definitive
                    && isqrtHashTag(i) == definitive) {
                continue;
            }
            Runtime.getRuntime().exit(1);
        }
        System.out.println("valid.");
        System.out.print("Please enter number of loops: ");
        int loops = (new Scanner(System.in)).nextInt();
        ThreadGroup trdGroup = new ThreadGroup("Computers");
        ArrayList<Computer> computers = new ArrayList<>();
        computers.add(new Computer(trdGroup, "sqrtIntNative", Main::sqrtIntNative, loops));
        computers.add(new Computer(trdGroup, "isqrt32", Main::isqrt32, loops));
        computers.add(new Computer(trdGroup, "sqrtInt", Main::sqrtInt, loops));
        computers.add(new Computer(trdGroup, "isqrt", Main::isqrt, loops));
        computers.add(new Computer(trdGroup, "isqrtHashTag", Main::isqrtHashTag, loops));
        computers.add(new Computer(trdGroup, "floorSqrt", Main::floorSqrt, loops));
        computers.forEach(Computer::start);
    }

    private static int sqrtInt(long n) {
        int c = 0x8000;
        int g = 0x8000;
        for (; ; ) {
            if (g * g > n) g ^= c;
            c >>= 1;
            if (c == 0) return g;
            g |= c;
        }
    }

    //tricking x to be unsigned
    private static int floorSqrt(int x) {
        return (int) StrictMath.sqrt(x & 0xffffffffL);
    }

    private static int sqrtIntNative(int n) {
        return (int) Math.sqrt(n);
    }

    private static int isqrt32(int n) {
        int root, remainder, place;

        root = 0;
        remainder = n;
        place = 0x40000000;

        while (place > remainder)
            place = place >> 2;
        while (place != 0) {
            if (remainder >= root + place) {
                remainder = remainder - root - place;
                root = root + (place << 1);
            }
            root = root >> 1;
            place = place >> 2;
        }
        return root;
    }

    private static int isqrt(int x) {
        int op, res, one;

        op = x;
        res = 0;

        one = 1 << 30;
        while (one > op) one >>= 2;

        while (one != 0) {
            if (op >= res + one) {
                op -= res + one;
                res += one << 1;
            }
            res >>= 1;
            one >>= 2;
        }
        return res;
    }

    private static int isqrtHashTag(int num) {
        if (0 == num) {
            return 0;
        }
        int n = (num / 2) + 1;
        int n1 = (n + (num / n)) / 2;
        while (n1 < n) {
            n = n1;
            n1 = (n + (num / n)) / 2;
        }
        return n;
    }

    private interface Algorithm {
        int exec(int n);
    }

    private static class Computer extends Thread {
        private Algorithm algorithm;
        private int loops;

        Computer(ThreadGroup group, String name, Algorithm algorithm, int loops) {
            super(group, name);
            this.algorithm = algorithm;
            this.loops = loops;
        }

        @Override
        public void run() {
            long cur = System.currentTimeMillis();
            for (int i = 0; i < loops; i++)
                algorithm.exec(i);
            long now = System.currentTimeMillis();
            System.out.println(getName() + ": " + (now - cur) + " millis");
        }
    }


}
