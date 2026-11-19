#include <bits/stdc++.h>
using namespace std;

#define int long long

const long double PI = 3.14159265359;

void solve()
{
    int arr[3];
    cin >> arr[0] >> arr[1] >> arr[2];
    sort(arr, arr + 3);
    if (arr[0] + 1 == arr[1] && arr[1] == arr[2] - 1)
        cout << "YES" << '\n';
    else
        cout << "NO" << '\n';
}

signed main(void)
{
    ios_base::sync_with_stdio(0);
    cin.tie(0);
    cout.tie(0);
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
