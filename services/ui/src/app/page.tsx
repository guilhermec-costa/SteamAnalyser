import { Routes } from "@/app/constants/routes";
import { steamAnalyserAPI } from "@/app/lib/axios";
import { MostPlayedItemWrapper } from "@/app/types/mostPlayedItem"
import MostPlayedTable from "@/app/components/mostPlayedTable/data-table";
import {columns} from "@/app/components/mostPlayedTable/columns";

export default async function Home() {
  const response = await steamAnalyserAPI.get<MostPlayedItemWrapper>(Routes.MOST_PLAYED);

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
          <MostPlayedTable columns={columns} data={response.data.content}/>
        </div>
      </section>
    </div>
  );
}
