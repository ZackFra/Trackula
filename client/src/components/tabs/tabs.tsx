"use client"
import { Tab, Tabs, TabList, TabPanel } from 'react-tabs';
import Dashboard from '@/components/dashboard';
import Timer from '@/components/timer';
import { IoIosRefresh } from "react-icons/io";
import 'react-tabs/style/react-tabs.css';
import useTimer from '@/hooks/use-timer';
import { useState } from 'react';
import { getTimerEntries } from '@/api/timer';

export default ({ timerEntries }: { timerEntries: TimerEntry[] }) => {
    const [isPaused, setIsPaused] = useState(false)
    const [time, resetTimer] = useTimer(isPaused)
    const toggleTimer = () => {
        setIsPaused(!isPaused)
    }
    const [text, setText] = useState("")
    const [fullName, setFullName] = useState("")

    const [timerEntriesInternal, setTimerEntries] = useState(timerEntries)

    const reset = () => {
        resetTimer()
        setIsPaused(true)
    }

    const refreshDashboard = async () => {
        const timerEntries = await getTimerEntries()
        setTimerEntries(timerEntries)
    }
    return (
        <Tabs>
            <TabList>
                <Tab>Timer</Tab>
                <Tab>Dashboard</Tab>
            </TabList>

            <TabPanel>
                <Timer
                    toggleTimer={toggleTimer}
                    time={time}
                    isPaused={isPaused}
                    resetTimer={reset}
                    text={text}
                    setText={setText}
                    fullName={fullName}
                    setFullName={setFullName}
                />
            </TabPanel>
            <TabPanel>
                <div className="flex justify-end">
                    <button onClick={refreshDashboard}>
                        <IoIosRefresh />
                    </button>
                </div>
                <Dashboard timerEntries={timerEntriesInternal} />
            </TabPanel>
        </Tabs>
    )
}