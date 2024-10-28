const getAllTimerEntries = () => {
    return fetch("/api/timer-entry", {
        method: "GET"
    })
}

export { getAllTimerEntries }