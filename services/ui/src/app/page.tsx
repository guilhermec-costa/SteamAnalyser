"use client"

import { Routes } from "@/constants/routes";
import { steamAnalyserAPI } from "@/lib/axios";
import { MostPlayedResponse } from "@/constants/steamAPIResponses"
import { Bar, BarChart, CartesianGrid, Tooltip, XAxis, YAxis, ResponsiveContainer, Legend } from "recharts";
import { useEffect, useState } from "react";
import DashboardTable from "@/components/DashboardTable";

export default function Home() {
  const [games, setGames] = useState<MostPlayedResponse>({content: []});

  useEffect(() => {
    const fetchGames = async () => {
      const response = await steamAnalyserAPI.get<MostPlayedResponse>(Routes.MOST_PLAYED);
      setGames(response.data);
    }
    fetchGames();
  }, []);

  return (
    <div className="min-h-screen bg-gradient-to-b from-gray-900 via-gray-800 to-black py-10 px-6">
      <header className="text-center mb-12">
        <h1 className="text-5xl font-bold text-transparent bg-clip-text bg-gradient-to-r from-green-400 to-blue-500 mb-2">
          Most Played Steam Games
        </h1>
        <p className="text-lg text-muted-foreground">
          Discover the most popular games being played on Steam right now. Track their peak players and current online status.
        </p>
      </header>

      <section className="w-full max-w-6xl mx-auto mb-12">
        <div className="bg-gray-800 bg-opacity-50 shadow-2xl rounded-lg p-6">
          <h2 className="text-2xl font-semibold text-white mb-4">Top Games List</h2>
          <div className="h-[1.2px] bg-gray-700 mb-3"></div>
          <DashboardTable games={games}/>
        </div>
      </section>

      {/* <section className="w-full max-w-6xl mx-auto">
        <div className="bg-gray-800 bg-opacity-50 shadow-2xl rounded-lg p-6">
          <h2 className="text-2xl font-semibold text-white mb-6">Current Players vs Peak Players</h2>
          <ResponsiveContainer width="100%" height={400}>
            <BarChart data={games.content}>
              <CartesianGrid strokeDasharray="3 3" stroke="#6b7280"/>
              <XAxis dataKey="name" tick={{ fill: '#ffffff' }} />
              <YAxis tick={{ fill: '#ffffff', fontSize: 14 }} />
              <Tooltip 
                contentStyle={{ backgroundColor: '#374151', borderRadius: '10px', border: 'none' }} 
                itemStyle={{ color: '#fff' }}
                cursor={{ fill: 'rgba(255, 255, 255, 0.2)' }}
              />
              <Legend wrapperStyle={{ color: '#ffffff' }} />
              <Bar dataKey="peakInGameNumber" fill="url(#colorPeak)" barSize={30} />
              <Bar dataKey="playersOnlineNumber" fill="url(#colorOnline)" barSize={30} />
              <defs>
                <linearGradient id="colorPeak" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#f59e0b" stopOpacity={0.9}/>
                  <stop offset="95%" stopColor="#f59e0b" stopOpacity={0.5}/>
                </linearGradient>
                <linearGradient id="colorOnline" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor="#10b981" stopOpacity={0.9}/>
                  <stop offset="95%" stopColor="#10b981" stopOpacity={0.5}/>
                </linearGradient>
              </defs>
            </BarChart>
          </ResponsiveContainer>
        </div>
      </section> */}
    </div>
  );
}
