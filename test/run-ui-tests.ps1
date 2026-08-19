[CmdletBinding()]
param(
    [string]$Filter = '',
    [switch]$Quiet
)

$ErrorActionPreference = 'Stop'

$testDir = $PSScriptRoot
$rootDir = Split-Path -Parent $testDir
$srcDir = Join-Path $rootDir 'src\main\java'
$binDir = Join-Path $rootDir 'out'
$planFile = Join-Path $testDir 'ui-test-plan.md'
$workDir = Join-Path ([System.IO.Path]::GetTempPath()) 'ev-ui-test'

function Read-TestPlan([string]$path) {
    $cases = @()
    $current = $null
    $section = ''
    $inFence = $false
    $buffer = @()

    foreach ($line in Get-Content -Path $path) {
        if ($line -match '^###\s+(TC-\d+)\s+(.*)$') {
            if ($null -ne $current) { $cases += $current }
            $current = [pscustomobject]@{
                Id       = $matches[1]
                Name     = $matches[2]
                Aim      = ''
                Input    = @()
                Expected = @()
            }
            $section = ''
            $inFence = $false
            continue
        }
        if ($null -eq $current) { continue }

        if ($line -match '^\*\*Aim:\*\*\s*(.*)$') {
            $current.Aim = $matches[1]
            $section = 'aim'
            continue
        }
        if ($line -match '^\*\*Input\*\*') { $section = 'input'; continue }
        if ($line -match '^\*\*Expected output\*\*') { $section = 'expected'; continue }

        if ($section -eq 'aim') {
            if ($line.Trim() -eq '') {
                $section = ''
            } else {
                $current.Aim = ($current.Aim + ' ' + $line.Trim()).Trim()
            }
            continue
        }

        if ($line -match '^```') {
            if ($inFence) {
                $inFence = $false
                if ($section -eq 'input') { $current.Input = $buffer }
                elseif ($section -eq 'expected') { $current.Expected = $buffer }
                $section = ''
                $buffer = @()
            } elseif ($section -ne '') {
                $inFence = $true
                $buffer = @()
            }
            continue
        }

        if ($inFence) { $buffer += $line }
    }

    if ($null -ne $current) { $cases += $current }
    return $cases
}

function Get-ReplyBody($outputLines, $caseId) {
    $lines = @($outputLines)
    $separatorIndexes = @()
    for ($i = 0; $i -lt $lines.Count; $i++) {
        if ($lines[$i] -match '^_+$') { $separatorIndexes += $i }
    }
    if ($separatorIndexes.Count -lt 4) {
        throw "$caseId : output does not contain the greeting and farewell frames."
    }

    $greetingEnd = $separatorIndexes[1]
    if ($lines[$separatorIndexes[0] + 1] -ne "Hello! I'm EV." -or
        $lines[$separatorIndexes[0] + 2] -ne 'What can I do for you?') {
        throw "$caseId : greeting does not match the one recorded in the test plan."
    }
    if ($lines[$lines.Count - 2] -ne 'Bye. Hope to see you again soon!') {
        throw "$caseId : farewell does not match the one recorded in the test plan."
    }

    $start = $greetingEnd + 1
    $end = $lines.Count - 4
    if ($start -gt $end) { return @() }
    return @($lines[$start..$end])
}

function Show-Lines($lines, $prefix) {
    foreach ($line in @($lines)) { Write-Host "$prefix$line" }
}

if (-not (Test-Path $planFile)) {
    Write-Host "Cannot find test plan: $planFile" -ForegroundColor Red
    exit 1
}
if (-not (Test-Path $workDir)) {
    New-Item -ItemType Directory -Path $workDir | Out-Null
}

$javac = (Get-Command javac -ErrorAction SilentlyContinue).Source
$java = (Get-Command java -ErrorAction SilentlyContinue).Source
if (-not $javac -or -not $java) {
    Write-Host 'javac or java not found on PATH.' -ForegroundColor Red
    exit 1
}

Write-Host "Compiling $srcDir ..."
$sources = @(Get-ChildItem -Path (Join-Path $srcDir '*.java') | ForEach-Object { $_.FullName })
& $javac -d $binDir $sources
if ($LASTEXITCODE -ne 0) {
    Write-Host 'Compilation failed.' -ForegroundColor Red
    exit 1
}

$cases = Read-TestPlan $planFile
if ($Filter -ne '') {
    $cases = @($cases | Where-Object { $_.Id -like "$Filter*" })
}
if ($cases.Count -eq 0) {
    Write-Host "No test cases matched filter '$Filter'." -ForegroundColor Yellow
    exit 1
}

Write-Host "Running $($cases.Count) test case(s) from $planFile"
Write-Host ''

$passed = 0
foreach ($case in $cases) {
    $inFile = Join-Path $workDir "$($case.Id).in.txt"
    $outFile = Join-Path $workDir "$($case.Id).out.txt"
    $errFile = Join-Path $workDir "$($case.Id).err.txt"

    Set-Content -Path $inFile -Value @($case.Input) -Encoding ascii

    Start-Process -FilePath $java `
        -ArgumentList @('-cp', $binDir, 'EV') `
        -RedirectStandardInput $inFile `
        -RedirectStandardOutput $outFile `
        -RedirectStandardError $errFile `
        -NoNewWindow -Wait | Out-Null

    $stderr = @(Get-Content -Path $errFile)
    if ($stderr.Count -gt 0) {
        Write-Host "FAILED $($case.Id) $($case.Name)" -ForegroundColor Red
        Write-Host 'The program wrote to standard error:'
        Show-Lines $stderr '  '
        exit 1
    }

    $actual = Get-ReplyBody (Get-Content -Path $outFile) $case.Id
    $expected = @($case.Expected)

    if (-not $Quiet) {
        Write-Host "$($case.Id) $($case.Name)" -ForegroundColor Cyan
        Write-Host "  Aim: $($case.Aim)"
        Show-Lines $case.Input '  > '
        Show-Lines $actual '  '
        Write-Host ''
    }

    $mismatch = -1
    $maxLines = [Math]::Max($expected.Count, $actual.Count)
    for ($i = 0; $i -lt $maxLines; $i++) {
        $e = if ($i -lt $expected.Count) { $expected[$i] } else { $null }
        $a = if ($i -lt $actual.Count) { $actual[$i] } else { $null }
        if ($e -ne $a) { $mismatch = $i; break }
    }

    if ($mismatch -ge 0) {
        Write-Host "FAILED $($case.Id) $($case.Name)" -ForegroundColor Red
        Write-Host "  Aim: $($case.Aim)"
        Write-Host "  First difference at reply line $($mismatch + 1):"
        $e = if ($mismatch -lt $expected.Count) { $expected[$mismatch] } else { '<no more output>' }
        $a = if ($mismatch -lt $actual.Count) { $actual[$mismatch] } else { '<no more output>' }
        Write-Host "    expected: $e" -ForegroundColor Yellow
        Write-Host "    actual  : $a" -ForegroundColor Yellow
        Write-Host ''
        Write-Host "  Full expected output ($($expected.Count) lines):"
        Show-Lines $expected '    '
        Write-Host "  Full actual output ($($actual.Count) lines):"
        Show-Lines $actual '    '
        Write-Host ''
        Write-Host "Stopped at the first failure. $passed case(s) passed before it." -ForegroundColor Red
        exit 1
    }

    $passed++
}

Write-Host "All $passed test case(s) passed." -ForegroundColor Green
exit 0
