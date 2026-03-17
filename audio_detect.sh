#!/system/bin/sh
# Audio Data Fetcher for Rooted Devices
# Get accurate hardware sample rate, buffer, and DAC info

get_hw_params() {
    # Find active PCM output
    for pcm in /proc/asound/card*/pcm*p; do
        if [ -d "$pcm/sub0" ]; then
            if grep -q "RUNNING" "$pcm/sub0/status" 2>/dev/null; then
                cat "$pcm/sub0/hw_params"
                return
            fi
        fi
    done
    echo "closed"
}

get_audio_flinger_info() {
    dumpsys media.audio_flinger | grep -A 20 "Output thread" | grep -m 1 "sample rate:"
}

get_dac_info() {
    cat /proc/asound/cards | grep -v "---"
}

case "$1" in
    "rate")
        get_hw_params | grep "rate" | awk '{print $2}'
        ;;
    "buffer")
        get_hw_params | grep "buffer_size" | awk '{print $2}'
        ;;
    "dac")
        get_dac_info
        ;;
    "flinger")
        get_audio_flinger_info
        ;;
    *)
        echo "Usage: $0 {rate|buffer|dac|flinger}"
        ;;
esac
