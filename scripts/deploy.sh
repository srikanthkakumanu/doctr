#!/usr/bin/env bash
set -euo pipefail

IMAGE="${1:-REPLACE_IMAGE}"
KUBE_CONFIG="${2:-$HOME/.kube/config}"

if [ "$IMAGE" = "REPLACE_IMAGE" ]; then
  echo "Usage: $0 <image> [kubeconfig]"
  exit 1
fi

if kubectl --kubeconfig="$KUBE_CONFIG" get deployment doctr >/dev/null 2>&1; then
  kubectl --kubeconfig="$KUBE_CONFIG" set image deployment/doctr doctr="$IMAGE"
else
  kubectl --kubeconfig="$KUBE_CONFIG" apply -f k8s
fi

echo "Deployed $IMAGE"
