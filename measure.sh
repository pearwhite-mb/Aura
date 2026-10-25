#!/usr/bin/env bash
set -euo pipefail

SCENARIO="${1:?usage: measure.sh <Scenario> [minutes] [sample_seconds]}"
MINUTES="${2:-10}"
SAMPLE="${3:-30}"
PKG="io.github.pearwhite.aura.sample"

# Reads one field of `dumpsys battery`. RU: Читает одно поле из `dumpsys battery`.
field() { adb shell dumpsys battery | tr -d '\r' | grep -i "^ *$1:" | head -1 | sed 's/.*: *//'; }

adb shell am force-stop "$PKG"
charge_before=$(field "Charge counter")
temp_before=$(field "temperature")
level_before=$(field "level")
adb shell am start -n "$PKG/$PKG.MainActivity" --es scenario "$SCENARIO" > /dev/null

temps=()
currents=()
end=$(( $(date +%s) + MINUTES * 60 ))
while [ "$(date +%s)" -lt "$end" ]; do
  sleep "$SAMPLE"
  temps+=("$(field temperature)")
  c=$(adb shell cat /sys/class/power_supply/battery/current_now 2>/dev/null | tr -d '\r' || true)
  if [[ "$c" =~ ^-?[0-9]+$ ]]; then currents+=("${c#-}"); fi
done

charge_after=$(field "Charge counter")
temp_after=$(field "temperature")
level_after=$(field "level")
adb shell am force-stop "$PKG"

drain=$(awk -v b="$charge_before" -v a="$charge_after" 'BEGIN { printf "%.1f", (b - a) / 1000 }')
mah_h=$(awk -v d="$drain" -v m="$MINUTES" 'BEGIN { printf "%.0f", d * 60 / m }')
temp_max=$(printf '%s\n' "${temps[@]:-$temp_after}" | sort -n | tail -1)
avg_ma=$(printf '%s\n' "${currents[@]:-}" | awk 'NF { s += $1; n++ } END { if (n) printf "%.0f", s / n / 1000; else print "n/a" }')
t() { awk -v x="$1" 'BEGIN { printf "%.1f", x / 10 }'; }

if [ ! -f results.csv ]; then
  echo "scenario,minutes,drain_mah,mah_per_hour,level,temp_start_c,temp_end_c,temp_max_c,avg_current_ma" > results.csv
fi
echo "$SCENARIO,$MINUTES,$drain,$mah_h,$level_before -> $level_after,$(t "$temp_before"),$(t "$temp_after"),$(t "$temp_max"),$avg_ma" | tee -a results.csv
