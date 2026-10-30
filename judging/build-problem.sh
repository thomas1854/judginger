# ==============================================================================
# Main-solution runner: generates the expected outputs for a problem's inputs
# $1: problem_id   $2: time limit (seconds)   $3: memory limit (MB)
# Output: CE | MLE-<test> | TLE-<test> | RE-<test> (prints nothing on success)
# ==============================================================================

# ---------- Compile the main solution (with sanitizers) ----------
g++ -O2 -std=c++20 -O1 -g -fsanitize=address,undefined  ./problems/"$1"/main.cpp -o ./problems/"$1"/main 2>/dev/null

status=$?

# Compilation error
if [ "$status" -ne 0 ]; then
    echo "CE"
    exit 1
fi

# ---------- Setup ----------
# Folder that will hold the expected outputs
mkdir ./problems/"$1"/output-main

# Remove stack size limit
ulimit -s unlimited

# ---------- Run on every test case ----------
for file in ./problems/"$1"/inputs/*.in; do
    [ -f "$file" ] || continue

    filename=$(basename "$file")

    # Run main solution in a memory-limited systemd scope, with a timeout,
    # recording "wall user sys memory" into stats.txt
    # and writing its output to the matching .out file
    systemd-run --user --quiet --no-ask-password --scope --unit="main_${1}" -p MemoryMax="${3}M" -p MemorySwapMax=0 --working-directory="$PWD" timeout "${2}s" /usr/bin/time -f "%e %U %S %M" -o ./problems/"$1"/stats.txt ./problems/"$1"/main < $file > ./problems/"$1"/output-main/"${filename%.in}".out

    status=$?

    # Read resource usage of this run
    read wall user sys mem < ./problems/"$1"/stats.txt

    # Clean up the systemd scope
    systemctl --user reset-failed "main_${1}.scope" 2>/dev/null

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

done