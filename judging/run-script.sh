# ==============================================================================
# Judge script
# $1: submission_id   $2: problem_id   $3: time limit (seconds)   $4: memory limit (MB)
# Output: CE | MLE-<test> | TLE-<test> | RE-<test> | WA-<test>-<time>-<mem> | AC-<time>-<mem>
# ==============================================================================

# ---------- Compile the submission ----------
g++ -O2 -std=c++20 ./submissions/"$1"/"$1".cpp -o ./submissions/"$1"/sub 2>/dev/null

status=$?

# Compilation error
if [ "$status" -ne 0 ]; then
    echo "CE"
    exit 1
fi

# ---------- Setup ----------
max_time=0.0
max_memory=0

# Remove stack size limit
ulimit -s unlimited

# ---------- Run on every test case ----------
for file in $(ls ./problems/"$2"/inputs/*.in | sort -V); do
    [ -f "$file" ] || continue

    filename=$(basename "$file")
    
    # Expected output, normalized to one token per line
    main_out=$(tr -s '[:space:]' '\n' < "./problems/"$2"/output-main/${filename%.in}.out" | sed '/^$/d')

    # Run submission in a memory-limited systemd scope, with a timeout,
    # recording "wall user sys memory" into stats.txt
    sub_out=$(systemd-run --user --quiet --no-ask-password --scope --unit="${1}" -p MemoryMax="${4}M" -p MemorySwapMax=0 --working-directory="$PWD" timeout "${3}s" /usr/bin/time -f "%e %U %S %M" -o ./submissions/"$1"/stats.txt ./submissions/"$1"/sub < "$file")
    status=$?

    # Read resource usage of this run
    read wall user sys mem < ./submissions/"$1"/stats.txt

    # Clean up the systemd scope
    systemctl --user reset-failed "${1}.scope" 2>/dev/null
    
    # ---------- Check verdict from exit status ----------
    if [ "$status" -eq 137 ]; then
        # Killed (memory limit exceeded)
        echo "MLE-${filename%.in}"
        exit 137
    elif [ "$status" -eq 143 ]; then
        # Terminated (memory limit exceeded)
        echo "MLE-${filename%.in}"
        exit 143
    elif [ "$status" -eq 124 ]; then
        # Timeout (time limit exceeded)
        echo "TLE-${filename%.in}"
        exit 124
    elif [ "$status" -ne 0 ]; then
        # Any other non-zero exit (runtime error)
        echo "RE-${filename%.in}"
        exit 1
    fi

    # ---------- Track maximum time and memory across tests ----------
    if awk -v a="$wall" -v b="$max_time" 'BEGIN { exit !(a > b) }'; then
        max_time=$wall
    fi

    if [ "$mem" -gt "$max_memory" ]; then
        max_memory=$mem
    fi

    #echo "wall=$wall user=$user sys=$sys memory=${mem}KB"

    # ---------- Compare outputs ----------
    # Normalize submission output the same way as expected output
    sub_out=$(printf '%s' "$sub_out" | tr -s '[:space:]' '\n' | sed '/^$/d')
    m=$m

    # Wrong answer
    if [ "$sub_out" != "$main_out" ]; then
        echo "WA-${filename%.in}-$max_time-$max_memory"
        exit 1
    fi
done

# ---------- All tests passed ----------
echo "AC-$max_time-$max_memory"