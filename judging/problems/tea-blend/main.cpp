// P1: https://codeforces.com/contest/2275/problem/F

#include <bits/stdc++.h>
using namespace std;

#define int long long

const long double PI = 3.14159265359;

const int MAX = 1e6;

vector<int> spf(MAX + 1);

void pre()
{
    for (int i = 1; i <= MAX; i++)
        spf[i] = i;

    for (int i = 2; i * i <= MAX; i++)
    {
        if (spf[i] == i)
        {
            for (int j = i * i; j <= MAX; j += i)
                if (spf[j] == j)
                    spf[j] = i;
        }
    }
}

void solve()
{
    int n;
    cin >> n;
    vector<int> arr(n);

    for (auto &val : arr)
        cin >> val;

    vector<vector<int>> kernel(n);

    for (int i = 0; i < n; i++)
    {
        int x = arr[i];
        while (x > 1)
        {
            int cnt = 0, p = spf[x];
            while (x > 1 && x % p == 0)
                x /= p, cnt++;
            if (cnt % 2)
                kernel[i].push_back(p);
        }
    }

    set<int> odd;
    map<vector<int>, int> mp;
    for (int i = 0; i < n; i++)
    {
        for (auto &val : kernel[i])
            if (!odd.insert(val).second)
                odd.erase(val);

        if (odd.size() <= 8)
            mp[vector<int>(odd.begin(), odd.end())]++;
    }

    int ans = 0;
    for (int i = 0; i < n; i++)
    {
        auto it = mp.find(kernel[i]);
        if (it != mp.end())
            ans += it->second;
    }
    cout << ans << '\n';
}

signed main(void)
{
    pre();
    if (fopen("input.txt", "r"))
    {
        freopen("input.txt", "r", stdin);
        freopen("output.txt", "w", stdout);
    }
    int t = 1;
    cin >> t;
    while (t--)
    {
        solve();
    }
    return 0;
}