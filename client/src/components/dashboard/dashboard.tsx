"use client"
import React from "react"

const Dashboard = ({ timerEntries }: { timerEntries: TimerEntry[] }) => {

    return (
        <div className="block">
            <h1>Dashboard</h1>
            <table>
                <thead>
                    <tr>
                        <th>Author</th>
                        <th>Time Tracked</th>
                    </tr>
                </thead>

                <tbody>
                    {timerEntries.map((entry, i) => (
                        <tr key={i}>
                            <td>{entry.author}</td>
                            <td>{entry.time}</td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div >
    )
}

export default Dashboard