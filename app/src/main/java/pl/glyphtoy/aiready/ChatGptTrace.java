package pl.glyphtoy.aiready;

import java.util.Arrays;

final class ChatGptTrace {
    private static final double[] ARRIVAL = build();
    static double arrival(int x, int y) { return ARRIVAL[y * 25 + x]; }

    private static double[] build() {
        double[] distance = new double[625]; Arrays.fill(distance, Double.POSITIVE_INFINITY);
        boolean[] visited = new boolean[625];
        int[][] starts = {{12, 9}, {15, 10}, {15, 14}, {12, 15}, {9, 14}, {9, 10}};
        for (int[] start : starts) {
            int nearest = -1; double best = Double.POSITIVE_INFINITY;
            for (int y = 0; y < 25; y++) for (int x = 0; x < 25; x++) {
                if (ChatGptSymbol.coverage(x, y) < .5) continue;
                double squared = (x - start[0]) * (x - start[0]) + (y - start[1]) * (y - start[1]);
                if (squared < best) { best = squared; nearest = y * 25 + x; }
            }
            if (nearest >= 0) distance[nearest] = 0;
        }
        double furthest = 0;
        for (int step = 0; step < 625; step++) {
            int current = -1; double nearest = Double.POSITIVE_INFINITY;
            for (int p = 0; p < 625; p++) if (!visited[p] && distance[p] < nearest) {
                nearest = distance[p]; current = p;
            }
            if (current < 0) {
                for (int p = 0; p < 625; p++) if (!visited[p] && ChatGptSymbol.coverage(p % 25, p / 25) > 0) {
                    current = p; distance[p] = furthest + .5; break;
                }
            }
            if (current < 0) break;
            visited[current] = true; furthest = Math.max(furthest, distance[current]);
            int x = current % 25, y = current / 25;
            for (int dy = -1; dy <= 1; dy++) for (int dx = -1; dx <= 1; dx++) {
                int nx = x + dx, ny = y + dy;
                if ((dx == 0 && dy == 0) || nx < 0 || ny < 0 || nx > 24 || ny > 24) continue;
                double coverage = ChatGptSymbol.coverage(nx, ny);
                if (coverage <= 0) continue;
                int next = ny * 25 + nx;
                double weight = Math.hypot(dx, dy) / (.2 + (coverage + ChatGptSymbol.coverage(x, y)) / 2);
                distance[next] = Math.min(distance[next], distance[current] + weight);
            }
        }
        for (int p = 0; p < 625; p++) distance[p] = Double.isFinite(distance[p]) ? .92 * distance[p] / furthest : 1;
        return distance;
    }
}
