// P3: https://codeforces.com/contest/2258/problem/B2

/*******************THOMAS*******************/
#include <bits/stdc++.h>

#define int long long

using namespace std;

void solve()
{
    int n, m; cin >> n >> m;
    vector<int> arr(n);
    map<int, int> freq;
    for (auto& val : arr) cin >> val, freq[val]++;

    int mx_k = min(18LL, m);
    vector<int> pre(m + 1, 0);

    for (int i = 1; i <= m; i++)
        pre[i] = pre[i - 1] + freq[i];

    vector<int> ans(m + 1);
    for (int k = 1; k <= mx_k; k++)
    {
        int max_contribution = 0;
        int cap = (1LL << k);
        for (int x = 1; x <= m; x++)
        {
            int current_contribtuion = 0;
            for (int v = 1; v <= m / x; v++)
            {
                int l = v * x;
                int r = min(m, (v + 1) * x - 1);
                int cnt = pre[r] - pre[l - 1];
                current_contribtuion += min(v, cap - 1) * cnt;
            }

            if (x * cap <= m)
            {
                int exact_len = x * cap;
                int exact_cnt = pre[exact_len] - pre[exact_len - 1];
                current_contribtuion += exact_cnt;
            }
            max_contribution = max(max_contribution, current_contribtuion);
        }
        ans[k] = max_contribution;
    }
    for (int i = 1; i <= mx_k; i++) cout << ans[i] << ' ';
    for (int i = mx_k + 1; i <= m; i++) cout << ans[mx_k] << ' ';
    cout << '\n';
}

signed main()
{
    ios_base::sync_with_stdio(false);
    cin.tie(0);
    cout.tie(0);

    int t = 1;
    cin >> t;
    while (t--)
    {
        solve();
    }

    return 0;
}