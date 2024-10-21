export const getTimerEntries = async () => {
    const res = await fetch("http://localhost:8080/timer", {
        method: "GET",
        headers: {
            "Content-Type": "application/json"
        },
        cache: 'no-store'
    });
    if (!res.ok) {
        throw new Error("Failed to fetch timer entries")
    }
    const body = await res.json();
    return body
}

export const saveTimerEntry = ({ author, description, time }: TimerEntry) => {
    return fetch("/api/insert", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            author,
            description,
            time
        })
    })
}