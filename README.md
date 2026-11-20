# Judginger

A Polygon / Codeforces-style online judge server built with **Spring Boot**. Submit a C++ solution to a problem, and Judginger compiles it, runs it against every test case under time and memory limits, compares the output with the reference solution, and returns a verdict.

> **Status:** personal project / work in progress. It is **not safe to expose to untrusted users yet**. See [Security notice](#security-notice).

---

## Features

- REST API for submitting solutions to a problem
- C++20 compilation with `g++`
- Per-test **time limit** (`timeout`) and **memory limit** (`systemd-run` scope with `MemoryMax`, swap disabled)
- Execution time and peak memory measured with `/usr/bin/time`
- Expected outputs generated automatically from the problem's **main (reference) solution**
- Token-based output comparison (whitespace-insensitive)
- Standard verdicts: `AC`, `WA`, `TLE`, `MLE`, `RE`, `CE`
- Submission history stored in a database through Spring Data JPA

## How it works

```
Client ──POST /problems/{problemId}/submissions──▶ Spring Boot (SubmissionService)
                                                       │
                          validates problem, file type (.cpp) and size
                                                       │
                       saves the file to judging/submissions/<uuid>/<uuid>.cpp
                                                       │
                                    runs  bash run-script.sh  via ProcessBuilder
                                                       │
              compile ─▶ for each test: run in limited scope ─▶ compare with expected output
                                                       │
                       verdict line is parsed and saved ─▶ JSON response ─▶ cleanup
```

### Judging pipeline

1. **Build the problem (once):** `build-problem.sh` compiles the problem's `main.cpp` (with AddressSanitizer and UBSan) and runs it on every `inputs/*.in` file to produce `output-main/*.out`.
2. **Judge a submission:** `run-script.sh` compiles the submission, then runs it on each input in natural order (`sort -V`). It stops at the first failing test.
3. **Report:** the script prints a single verdict line, which `SubmissionService` parses into the API response.

### Verdicts

| Verdict | Meaning |
|---------|---------|
| `AC`  | Accepted: all tests passed |
| `WA`  | Wrong answer on a specific test |
| `TLE` | Time limit exceeded on a specific test |
| `MLE` | Memory limit exceeded on a specific test |
| `RE`  | Runtime error (non-zero exit code) on a specific test |
| `CE`  | Compilation error |

Script output format:

```
CE | MLE-<test> | TLE-<test> | RE-<test> | WA-<test>-<time>-<mem> | AC-<time>-<mem>
```

## Project layout

```
judginger/
├── src/main/java/project/judginger/
│   ├── services/SubmissionService.java   # saves the file, calls the judge script, parses the verdict
│   ├── dto/                              # SubmissionRequest, SubmissionResponse
│   ├── model/                            # Submission entity
│   ├── repository/                       # ProblemRepository, SubmissionRepository
│   └── exception/                        # ResourceNotFound, InvalidFileType, FileSizeLimitExceeded
└── judging/
    ├── build-problem.sh                  # generates expected outputs from main.cpp
    ├── run-script.sh                     # compiles and judges a submission
    ├── problems/
    │   └── <problemId>/
    │       ├── main.cpp                  # reference solution
    │       ├── inputs/                   # 1.in, 2.in, ...
    │       └── output-main/              # generated expected outputs
    └── submissions/
        └── <uuid>/                       # temporary working directory, deleted after judging
```

## Requirements

- Linux with **systemd** and a working **user session** (`systemd-run --user`), with cgroup v2 memory control available to user units
- `g++` with C++20 support
- GNU `time` at `/usr/bin/time`
- `bash`, `awk`, `sed`, `tr`, `timeout` (coreutils)
- Java and Maven/Gradle matching your Spring Boot version
- A database supported by Spring Data JPA

## Getting started

### 1. Clone and configure

```bash
git clone https://github.com/<your-username>/judginger.git
cd judginger
```

Configure your datasource in `src/main/resources/application.properties`.

The judging directory is currently **hard-coded** in `SubmissionService`:

```
/home/thomas/thomas/judginger/judging
```

Change it to match your machine before running (moving it into `application.properties` is on the roadmap).

### 2. Add a problem

```bash
mkdir -p judging/problems/<problemId>/inputs
# put the reference solution in judging/problems/<problemId>/main.cpp
# put the test inputs in judging/problems/<problemId>/inputs/1.in, 2.in, ...
```

Register the problem in the database so its key exists (`problemRepository.existsByProblemKey`).

### 3. Generate the expected outputs

Run from inside the `judging` directory:

```bash
cd judging
bash build-problem.sh <problemId> <timeLimitSeconds> <memoryLimitMB>
```

It prints nothing on success, or `CE`, `MLE-<test>`, `TLE-<test>`, `RE-<test>` if the reference solution fails. If `output-main` already exists, delete it before rebuilding.

### 4. Run the server

```bash
./mvnw spring-boot:run
# or
./gradlew bootRun
```

## API

### Submit a solution

`POST /problems/{problemId}/submissions` (multipart form data)

| Part | Description |
|------|-------------|
| file | The C++ source file (must have the `.cpp` extension, max 2 MB) |
| request | JSON with `time_limit` (seconds) and `memory_limit` (MB) |

```bash
curl -X POST http://localhost:8080/problems/<problemId>/submissions \
  -F "file=@solution.cpp" \
  -F 'request={"time_limit": 2, "memory_limit": 256};type=application/json'
```

Example responses:

```json
{ "submissionId": "3f2c...", "verdict": "AC", "time": 412, "memory": 3520 }
```

```json
{ "submissionId": "9a1b...", "verdict": "WA", "test": "7", "time": 380, "memory": 3484 }
```

| Field | Unit / meaning |
|-------|----------------|
| `time` | Maximum wall time across tests, in milliseconds |
| `memory` | Peak memory across tests, in KB |
| `test` | The test that produced a non-AC verdict |

> Adjust the multipart part names above to match your controller.

## Security notice

Judginger currently runs submitted code **as the server's own user, with no sandbox**. A malicious submission could read your files (including the expected outputs), use the network, flood output, or fork-bomb the host. Only use it with code you trust, for example for personal practice, a private class, or local testing. Do not expose it publicly until sandboxing is in place.

## Known limitations

- Judging is **synchronous**, so each request blocks until all tests finish
- Timing uses wall-clock time, so it is affected by machine load
- Compilation has no time or memory limit
- Output comparison is token-based only (no floating-point tolerance or custom checkers)
- Verdict parsing splits on `-`, so test file names containing `-` break it
- Judging directory path is hard-coded

## Contributing

Issues and pull requests are welcome. Please open an issue first to discuss larger changes.

## Related Repository

[Judginger Frontend](https://github.com/thomas1854/judginger-web) - Web interface for the Judginger online judge platform. Run the backend server and use the web interface to interact with the system.
