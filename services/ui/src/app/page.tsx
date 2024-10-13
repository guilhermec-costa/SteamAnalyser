import { Routes } from "@/constants/routes";
import { steamAnalyserAPI } from "@/lib/axios";
import { MostPlayedResponse } from "@/constants/steamAPIResponses"
import { Table, TableBody, TableCaption, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";

const Home = async () => {
  const games = await steamAnalyserAPI.get<MostPlayedResponse[]>(Routes.MOST_PLAYED);

  return (
    <div className="bg-background">
      <section className="flex justify-center">
        <Table className="mx-auto w-[40%] bg-secondary rounded-md">
          <TableCaption>Most Played Games</TableCaption>
          <TableHeader className="p-3">
            <TableRow>
              <TableHead
                className="font-normal text-foreground tracking-wide text-lg">Most Played Games</TableHead>
              <TableHead
                className="font-normal text-foreground tracking-wide text-center text-md">Online Players</TableHead>
              <TableHead
                className="font-normal text-foreground tracking-wide text-center text-md">Players Peak</TableHead>
            </TableRow>
          </TableHeader>
          <TableBody>
            {games.data.map(game => (
              <TableRow className="h-[16px] border-b-2 border-secondary">
                <TableCell className="w-[70%] p-1">
                  <section className="flex flex-row items-center gap-x-3">
                    <img src={game.gameHeaderImage} className="w-24" />
                    {game.name}
                  </section>
                </TableCell>
                <TableCell
                  className="text-green-700 font-bold text-center w-[15%]">{game.playersOnline}</TableCell>
                <TableCell className="w-[15%] text-center">{game.peakInGame}</TableCell>
              </TableRow>
            ))}
          </TableBody>
        </Table>
      </section>
    </div>
  );
}

export default Home;
