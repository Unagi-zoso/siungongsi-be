#!/bin/bash
echo "[HOOK] Restarting healthd..."
sudo systemctl restart healthd || true
