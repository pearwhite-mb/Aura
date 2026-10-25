#!/usr/bin/env bash
# Summarizes results.csv: mean, spread and difference from the static baseline per scenario.
# RU: Сводка по results.csv: среднее, разброс и разница с базовой линией по каждому сценарию.
set -euo pipefail
export LC_ALL=C   # dot as the decimal separator, regardless of the system locale

FILE="${1:-results.csv}"
BASE="${2:-FillFullScreenStatic}"

[ -f "$FILE" ] || { echo "File not found: $FILE" >&2; exit 1; }

# `tr` removes Windows line endings (CRLF), which would otherwise stick to the last column.
# RU: `tr` убирает окончания строк Windows (CRLF), иначе они прилипнут к последней колонке.
tr -d '\r' < "$FILE" | awk -F, -v base="$BASE" '
$1 == "scenario" { next }          # header (also skips repeated headers)
NF < 9           { next }          # broken or incomplete line
{
  name = $1; v = $4 + 0; t = $8 + 0
  if (!(name in n)) { order[++count] = name; lo[name] = v; hi[name] = v; tmax[name] = t }
  n[name]++; sum[name] += v
  if (v < lo[name]) lo[name] = v
  if (v > hi[name]) hi[name] = v
  if (t > tmax[name]) tmax[name] = t
}
END {
  hasBase = (base in n)
  if (hasBase) baseMean = sum[base] / n[base]
  printf "%-26s %4s %10s  %-13s %10s %9s\n", "scenario", "runs", "mAh/h mean", "(min..max)", "vs static", "temp max"
  for (i = 1; i <= count; i++) {
    name  = order[i]
    mean  = sum[name] / n[name]
    spread = sprintf("(%.0f..%.0f)", lo[name], hi[name])
    delta = hasBase ? sprintf("%+.0f", mean - baseMean) : "n/a"
    printf "%-26s %4d %10.0f  %-13s %10s %9.1f\n", name, n[name], mean, spread, delta, tmax[name]
  }
}'
