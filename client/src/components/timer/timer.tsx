"use client"
import { saveTimerEntry } from "@/api/timer"
import React from "react"
import { getTime } from "@/components/timer/utils"

type TimerProps = {
    time: number
    toggleTimer: () => void
    isPaused: boolean
    resetTimer: () => void
    text: string
    setText: (text: string) => void
    fullName: string
    setFullName: (fullName: string) => void
}
const Timer = ({
    time,
    toggleTimer,
    isPaused,
    resetTimer,
    text,
    setText,
    fullName,
    setFullName
}: TimerProps) => {
    const onPause = () => {
        toggleTimer()
    }

    const onSave = () => {
        resetTimer()
        setText("")
        saveTimerEntry({
            author: fullName,
            description: text,
            time
        })
    }

    return (
        <div>
            <h1>Time Tracked: {getTime(time)}</h1>
            <br />
            <label
                htmlFor="fullName"
                className="block"
            >Full Name</label>
            <input
                id="fullName"
                type="text"
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                className="border border-gray-300 dark:border-gray-700 rounded-lg p-2 w-full"
            />
            <br />
            <label
                htmlFor="description"
                className="block"
            >
                Description
            </label>
            <textarea
                id="description"
                placeholder="Write your thoughts here..."
                value={text}
                onChange={(e) => setText(e.target.value)}
                className="border border-gray-300 dark:border-gray-700 rounded-lg p-2 w-full"
            />
            <br /><br />
            <div className="flex justify-around">
                <button
                    className="btn btn-primary text-white"
                    type="button"
                    onClick={onPause}>{isPaused ? "Resume" : "Pause"}
                </button>

                <button
                    type="submit"
                    onClick={onSave}
                    className="btn btn-success text-white"
                >Save</button>
            </div>
        </div >
    )
}
export default Timer