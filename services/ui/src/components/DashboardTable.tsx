import { Table, TableBody, TableCaption, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";
import { MostPlayedResponse } from "@/constants/steamAPIResponses";
import { Circle, SquareArrowOutUpRight, Users } from "lucide-react";
import Link from "next/link";

export default function DashboardTable({ games }: { games: MostPlayedResponse }) {
  return (
    <Table>
      <TableHeader>
        <TableRow className="border-none">
          <TableHead
            className="py-3 px-3 font-medium text-muted-foreground tracking-wide text-base">Most Played Games</TableHead>
          <TableHead
            className="font-medium text-muted-foreground tracking-wide text-center text-base">Current Players</TableHead>
          <TableHead
            className="font-medium text-muted-foreground tracking-wide text-center text-base">24h Peak</TableHead>
        </TableRow>
      </TableHeader>
      <TableBody className="mt-3">
        {games.content.map(game => (
          <TableRow className="h-[16px] border-b-[1px] border-slate-700">
            <TableCell className="w-[70%] p-0 px-3">
              <section className="flex flex-row items-center gap-x-3">
                <Link href={`/game/${game.name}`} className="flex items-center gap-x-1">
                  <img src={game.appImage} className="w-24" />
                  <p className="tracking-wide text-primary ml-1">{game.name}</p>
                  <SquareArrowOutUpRight size={14} className="text-muted-foreground hover:cursor-pointer" />
                </Link>
              </section>
            </TableCell>
            <TableCell
              className="font-normal text-center w-[15%]">
              <div className="flex items-center justify-center gap-3">
                <Circle size={13} className="bg-emerald-500 rounded-full" stroke="none" />
                <p className="tracking-wide text-md">{game.current_players}</p>
              </div>
            </TableCell>
            <TableCell className="w-[15%] text-center text-md tracking-wide">
              <div className="flex items-center justify-center gap-2">
                <Users size={14} className="text-muted-foreground" />
                {game._24hpeak}
              </div>
            </TableCell>
          </TableRow>
        ))}
      </TableBody>
    </Table>
  )
}