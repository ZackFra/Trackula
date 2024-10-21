export const getTime = (timeInSeconds: number): string => {
    if (timeInSeconds < 10) {
        return `00:00:${padLeftWithZero(timeInSeconds)}`
    } else if (timeInSeconds < 3600) {
        const minutes = padLeftWithZero(Math.floor(timeInSeconds / 60))
        const seconds = padLeftWithZero(timeInSeconds % 60)
        return `00:${minutes}:${seconds}`
    } else {
        const hours = padLeftWithZero(Math.floor(timeInSeconds / 3600))
        const minutes = padLeftWithZero(Math.floor((timeInSeconds % 3600) / 60))
        const seconds = padLeftWithZero(timeInSeconds % 60)
        return `${hours}:${minutes}:${seconds}`
    }
}

const padLeftWithZero = (num: number): string => {
    return ("" + num).padStart(2, "0")
}
