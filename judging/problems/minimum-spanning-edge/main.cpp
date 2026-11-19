// P2: https://codeforces.com/contest/609/problem/E

/*******************THOMAS*******************/
#include <bits/stdc++.h>

#define int long long

using namespace std;

void init()
{
    ios_base::sync_with_stdio(false);
    cin.tie(nullptr);
}

const int dx[] = { -1, 0, 0, 1, -1, -1, 1, 1 };
const int dy[] = { 0, -1, 1, 0, -1, 1, -1, 1 };

const long double PI = 3.14159265359;

const int INF = 2e9;

const int MOD = 1e9 + 7;
const int N = 2e6 + 5;

class DSU
{
private:
    vector<int> parent, sizes;
    vector<pair<int&, int>> history;
public:
    DSU(int size) : parent(size + 1, 1), sizes(size + 1, 1)
    {
        iota(parent.begin(), parent.end(), 0);
    }

    int find_set(int v)
    {
        if (v == parent[v]) return v;
        return parent[v] = find_set(parent[v]);
    }

    void union_sets(int a, int b)
    {
        a = find_set(a);
        b = find_set(b);
        if (a != b)
        {
            if (sizes[a] < sizes[b]) swap(a, b);
            history.push_back({ parent[b], parent[b] });
            history.push_back({ sizes[a], sizes[a] });
            parent[b] = a;
            sizes[a] += sizes[b];
        }
    }

    void rollback()
    {
        history.back().first = history.back().second;
        history.pop_back();
        history.back().first = history.back().second;
        history.pop_back();
    }
};

struct Edge
{
    int to, weight;
};


class LCA {
private:
    int n;
    int log_n;
    std::vector<int> depth;
    std::vector<int> sz;
    std::vector<std::vector<int>> up;
    std::vector<std::vector<int>> max_edge;
    std::vector<std::vector<int>> min_edge;

    void dfs(int v, int p, int w, const std::vector<std::vector<Edge>>& adj) {
        up[v][0] = p;
        sz[v] = 1;
        max_edge[v][0] = w;
        min_edge[v][0] = w;
        for (int i = 1; i <= log_n; ++i) {
            up[v][i] = up[up[v][i - 1]][i - 1];
            max_edge[v][i] = max(max_edge[v][i - 1], max_edge[up[v][i - 1]][i - 1]);
            min_edge[v][i] = max(min_edge[v][i - 1], min_edge[up[v][i - 1]][i - 1]);
        }

        for (Edge u : adj[v]) {
            if (u.to != p) {
                depth[u.to] = depth[v] + 1;
                dfs(u.to, v, u.weight, adj);
                sz[v] += sz[u.to];
            }
        }
    }

public:
    // Constructor initializes the structures and builds the lifting table.
    LCA(int n, int root, const std::vector<std::vector<Edge>>& adj) {
        this->n = n;
        this->log_n = std::ceil(std::log2(n)) + 1;
        depth.assign(n + 1, 0);
        sz.assign(n + 1, 0);
        up.assign(n + 1, std::vector<int>(log_n + 1, root));
        min_edge.assign(n + 1, std::vector<int>(log_n + 1, INF));
        max_edge.assign(n + 1, std::vector<int>(log_n + 1, -INF));
        dfs(root, root, INF, adj);
    }

    // Returns the lowest common ancestor of nodes u and v
    int get_lca(int u, int v) const {
        if (depth[u] < depth[v]) {
            std::swap(u, v);
        }

        // 1. Lift u to the same depth as v
        int diff = depth[u] - depth[v];
        for (int i = log_n; i >= 0; --i) {
            if ((diff >> i) & 1) {
                u = up[u][i];
            }
        }

        if (u == v) return u;

        // 2. Lift both nodes together right below their LCA
        for (int i = log_n; i >= 0; --i) {
            if (up[u][i] != up[v][i]) {
                u = up[u][i];
                v = up[v][i];
            }
        }

        // The parent of either node is now the LCA
        return up[u][0];
    }

    // NEW: Returns the k-th ancestor of node u. 
    // Returns -1 if the ancestor doesn't exist (k > depth of u).
    int get_kth_ancestor(int u, int k) const {
        if (depth[u] < k) {
            return -1;
        }
        for (int i = 0; i <= log_n; ++i) {
            if ((k >> i) & 1) {
                u = up[u][i];
            }
        }
        return u;
    }

    bool is_ancestor(int u, int v) const {
        // If u is deeper than v, it cannot be an ancestor
        if (depth[u] > depth[v]) {
            return false;
        }
        // Check if the ancestor of v at the distance (depth[v] - depth[u]) is u
        return get_kth_ancestor(v, depth[v] - depth[u]) == u;
    }

    // Helper: Returns the distance between two nodes in an unweighted tree
    int get_dist(int u, int v) const {
        return depth[u] + depth[v] - 2 * depth[get_lca(u, v)];
    }

    // Helper: Returns the depth of a node
    int get_depth(int u) const {
        return depth[u];
    }

    // Helper: Returns the size of the subtree rooted at u
    int get_size(int u) const
    {
        return sz[u];
    }

    pair<int, int> query(int u, int v)
    {
        int min_res = INF;
        int max_res = -INF;

        if (depth[u] < depth[v]) swap(u, v);
        int diff = depth[u] - depth[v];
        for (int j = 0; j < log_n; j++)
        {
            if ((diff >> j) & 1)
            {
                min_res = min(min_res, min_edge[u][j]);
                max_res = max(max_res, max_edge[u][j]);
                u = up[u][j];
            }
        }

        if (u == v) return { min_res, max_res };

        for (int j = log_n - 1; j >= 0; j--)
        {
            if (up[u][j] != up[v][j])
            {
                min_res = min({ min_res, min_edge[u][j], min_edge[v][j] });
                max_res = max({ max_res, max_edge[u][j], max_edge[v][j] });
                u = up[u][j];
                v = up[v][j];
            }
        }
        min_res = min({ min_res, min_edge[u][0], min_edge[v][0] });
        max_res = max({ max_res, max_edge[u][0], max_edge[v][0] });
        return { min_res, max_res };
    }
};

void solve()
{
    int n, m; cin >> n >> m;
    vector<array<int, 4>> edges(m);
    map<int, int> visited;
    for (int i = 0; i < m; i++)
    {
        cin >> edges[i][0] >> edges[i][1] >> edges[i][2];
        edges[i][0]--, edges[i][1]--;
        edges[i][3] = i;
    }

    sort(edges.begin(), edges.end(), [&](auto& a1, auto& a2) {
        return a1[2] < a2[2];
        });

    int curr_cost = 0;
    DSU dsu(n);
    vector<vector<Edge>> adj(n, vector<Edge>());
    for (auto& e : edges)
    {
        int u = e[0], v = e[1], w = e[2], idx = e[3];
        if (dsu.find_set(u) != dsu.find_set(v))
        {
            visited[idx] = 1;
            curr_cost += w;
            dsu.union_sets(u, v);
            adj[u].push_back({ v, w });
            adj[v].push_back({ u, w });
        }
    }

    LCA lca(n + 1, 0, adj);
    sort(edges.begin(), edges.end(), [&](auto& a1, auto& a2) {
        return a1[3] < a2[3];
        });
    vector<int> ans(m);
    for (int i = 0; i < m; i++)
    {
        auto& e = edges[i];
        int u = e[0], v = e[1], w = e[2], idx = e[3];
        if (visited[idx]) ans[i] = curr_cost;
        else ans[i] = curr_cost - (lca.query(u, v).second) + w;
    }

    for (auto& val : ans) cout << val << '\n';
}

signed main()
{
    init();

    int t = 1;
    // cin >> t;
    while (t--)
    {
        solve();
    }

    return 0;
}