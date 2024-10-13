import { Routes } from "@/constants/routes";
import { steamAnalyserAPI } from "@/lib/axios";
import { MostPlayedResponse } from "@/constants/steamAPIResponses"
import { Table, TableBody, TableCaption, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { Circle, SquareArrowOutUpRight, Users } from "lucide-react"
import Link from "next/link";

const Home = async () => {
  const games = await steamAnalyserAPI.get<MostPlayedResponse[]>(Routes.MOST_PLAYED);

  return (
    <div>
      <section className="w-1/2 mx-auto flex justify-center rounded-md border border-secondary mt-4">
        <Table>
          <TableHeader>
            <TableRow className="border-none">
              <TableHead
                className="py-3 px-3 font-medium text-primary tracking-wide text-base">Most Played Games</TableHead>
              <TableHead
                className="font-medium text-primary tracking-wide text-center text-base">Current Players</TableHead>
              <TableHead
                className="font-medium text-primary tracking-wide text-center text-base">Players Peak</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody className="mt-3">
            {games.data.map(game => (
              <TableRow className="h-[16px] border-b-[1px] border-slate-700">
                <TableCell className="w-[70%] p-0 px-3">
                  <section className="flex flex-row items-center gap-x-3">
                    <Link href={`/game/${game.steamAppId}`} className="flex items-center gap-x-1">
                      <img src={game.gameHeaderImage} className="w-[120px]" />
                      <p className="tracking-wide text-primary ml-1">{game.name}</p>
                      <SquareArrowOutUpRight size={14} className="text-muted-foreground hover:cursor-pointer" />
                    </Link>
                  </section>
                </TableCell>
                <TableCell
                  className="font-normal text-center w-[15%]">
                  <div className="flex items-center justify-center gap-3">
                    <Circle size={13} className="bg-emerald-500 rounded-full" stroke="none" />
                    <p className="tracking-wide text-md">{game.playersOnline}</p>
                  </div>
                </TableCell>
                <TableCell className="w-[15%] text-center text-md tracking-wide">
                  <div className="flex items-center justify-center gap-2">
                    <Users size={14} className="text-muted-foreground"/>
                    {game.peakInGame}
                  </div>
                  </TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </section>
    </div>
  );
}

export default Home;
