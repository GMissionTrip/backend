package com.gangchu.gangchutrip.route.algorithm;

import com.gangchu.gangchutrip.route.dto.CoordinateDto;
import com.gangchu.gangchutrip.route.dto.RouteRequestDto;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class TspSolver {
    private static final double INF = Double.MAX_VALUE / 2;

    public List<CoordinateDto> solveTsp(RouteRequestDto request) {
        CoordinateDto origin = request.getOrigin();
        CoordinateDto destination = request.getDestination();
        List<CoordinateDto> waypoints = request.getWaypoints();

        if (waypoints == null || waypoints.isEmpty()) {
            return Collections.emptyList();
        }

        List<CoordinateDto> nodes = buildNodes(origin, waypoints);
        int n = waypoints.size();

        Map<Integer, Integer> orderConstraint = new HashMap<>();
        Map<Integer, Integer> stepToNodeMap = new HashMap<>();
        parseOrderConstraints(waypoints, orderConstraint, stepToNodeMap);

        double[][] dist = buildDistanceMatrix(nodes);
        double[][] dp = new double[1 << (n + 1)][n + 1];
        int[][] prev = new int[1 << (n + 1)][n + 1];
        initDpPrev(dp, prev);

        dp[1][0] = 0;

        for (int mask = 1; mask < (1 << (n + 1)); mask++) {
            for (int u = 0; u <= n; u++) {
                if ((mask & (1 << u)) == 0 || dp[mask][u] == INF) continue;
                int step = Integer.bitCount(mask);

                for (int v = 1; v <= n; v++) {
                    if ((mask & (1 << v)) != 0) continue;
                    int nextStep = step + 1;
                    if (orderConstraint.containsKey(v) && orderConstraint.get(v) != nextStep) continue;
                    if (stepToNodeMap.containsKey(nextStep) && stepToNodeMap.get(nextStep) != v) continue;

                    double cost = dp[mask][u] + dist[u][v];
                    int nextMask = mask | (1 << v);

                    if (cost < dp[nextMask][v]) {
                        dp[nextMask][v] = cost;
                        prev[nextMask][v] = u;
                    }
                }
            }
        }

        int finalMask = (1 << (n + 1)) - 1;
        double minCost = INF;
        int last = -1;
        for (int i = 1; i <= n; i++) {
            if (dp[finalMask][i] < minCost) {
                minCost = dp[finalMask][i];
                last = i;
            }
        }

        if (minCost == INF) {
            return Collections.emptyList();
        }

        List<CoordinateDto> result = restorePath(nodes, prev, finalMask, last);
        result.add(destination);

        return result.subList(1, result.size() - 1);
    }

    private List<CoordinateDto> buildNodes(CoordinateDto origin, List<CoordinateDto> waypoints) {
        List<CoordinateDto> nodes = new ArrayList<>();
        nodes.add(origin);
        nodes.addAll(waypoints);
        return nodes;
    }

    private void parseOrderConstraints(List<CoordinateDto> waypoints, Map<Integer, Integer> orderConstraint, Map<Integer, Integer> stepToNodeMap) {
        for (int i = 0; i < waypoints.size(); i++) {
            CoordinateDto wp = waypoints.get(i);
            if (wp.getOrder() != null) {
                int nodeIndex = i + 1;
                int step = wp.getOrder() + 2;
                orderConstraint.put(nodeIndex, step);
                stepToNodeMap.put(step, nodeIndex);
            }
        }
    }

    private double[][] buildDistanceMatrix(List<CoordinateDto> nodes) {
        int n = nodes.size();
        double[][] dist = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                dist[i][j] = (i == j) ? 0 : distance(nodes.get(i), nodes.get(j));
            }
        }
        return dist;
    }

    private void initDpPrev(double[][] dp, int[][] prev) {
        for (double[] row : dp) {
            Arrays.fill(row, INF);
        }
        for (int[] row : prev) {
            Arrays.fill(row, -1);
        }
    }

    private List<CoordinateDto> restorePath(List<CoordinateDto> nodes, int[][] prev, int mask, int curr) {
        List<CoordinateDto> path = new ArrayList<>();
        while (curr != -1) {
            path.add(nodes.get(curr));
            int prevNode = prev[mask][curr];
            mask ^= (1 << curr);
            curr = prevNode;
        }
        Collections.reverse(path);
        return path;
    }

    private double distance(CoordinateDto a, CoordinateDto b) {
        double dx = a.getX() - b.getX();
        double dy = a.getY() - b.getY();
        return Math.sqrt(dx * dx + dy * dy);
    }
}