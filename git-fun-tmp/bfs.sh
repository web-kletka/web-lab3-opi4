#!/usr/bin/env bash
set -euo pipefail

cd src

TO=$1
FROM=$2

declare -A PARENT_OF
declare -A VISITED

QUEUE=("$FROM")
VISITED["$FROM"]=1
FOUND=0

while [ ${#QUEUE[@]} -gt 0 ]; do
  NEXT_QUEUE=()

  for COMMIT in "${QUEUE[@]}"; do
    if [ "$COMMIT" = "$TO" ]; then
      FOUND=1
      break 2
    fi

    # shellcheck disable=SC2034
    NEIGHBOURS=$(git log -n1 --pretty=%P "$COMMIT" 2>/dev/null || echo "")
    for P in $NEIGHBOURS; do
      if [ -z "${VISITED[$P]:-}" ]; then
        VISITED["$P"]=1
        NEXT_QUEUE+=("$P")
        PARENT_OF["$P"]="$COMMIT"
      fi
    done
  done

  QUEUE=("${NEXT_QUEUE[@]}")
done

if [ "$FOUND" -eq 1 ]; then
  PATH_COMMITS=()
  CUR="$TO"
  while [ "$CUR" != "$FROM" ]; do
    PATH_COMMITS+=("$CUR")
    CUR="${PARENT_OF[$CUR]}"
    if [ -z "$CUR" ]; then
      echo "Error: broken path"
      exit 1
    fi
  done
  PATH_COMMITS+=("$FROM")

  echo "Путь из $(git log -1 --pretty=format:'%s' "$TO") в $(git log -1 --pretty=format:'%s' "$FROM"):"
  for ((i=0; i < ${#PATH_COMMITS[@]}; i++)); do
    HASH="${PATH_COMMITS[$i]}"
    echo "$(git log -1 --pretty=format:'%s' "$HASH")"
  done
else
  echo "Не существует пути из $TO to $FROM found."
  exit 1
fi

