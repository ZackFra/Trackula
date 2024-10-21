"use client"
import { useEffect, useState } from 'react'
const useTimer = (isPaused: boolean): [number, () => void] => {
    const [time, setTime] = useState(0)
    const [lastTimeStamp, setLastTimeStamp] = useState(now())
    useEffect(() => {
        const handle = setInterval(() => {
            if (isPaused) {
                clearInterval(handle)
            }
            const currTimeStamp = now()
            const delta = currTimeStamp - lastTimeStamp
            if (delta > 1000) {
                setTime(time + Math.floor(delta / 1000))
                setLastTimeStamp(currTimeStamp)
            }
        }, 100)
        return () => {
            if (handle) {
                clearInterval(handle)
            }
        }
    }, [time, lastTimeStamp, isPaused])
    const resetTimer = () => {
        setTime(0)
        setLastTimeStamp(now())
    }
    return [time, resetTimer]
}

interface Performance {
    now?: () => number,
    mozNow?: () => number,
    msNow?: () => number,
    oNow?: () => number,
    webkitNow?: () => number
}

const now = () => {
    const perf = performance as Performance
    if (globalThis.performance.now) {
        return globalThis.performance.now()
    } else if (perf.mozNow) {
        return perf.mozNow()
    } else if (perf.msNow) {
        return perf.msNow()
    } else if (perf.oNow) {
        return perf.oNow()
    } else if (perf.webkitNow) {
        return perf.webkitNow()
    }
    return Date.now()
}

export default useTimer