import { Routes } from "@/constants/routes";
import { steamAnalyserAPI } from "@/lib/axios";
import { MostPlayedResponse } from "@/constants/steamAPIResponses"
import MostPlayedItem from "@/components/MostPlayedItem";
import { Table, TableBody, TableCaption, TableCell, TableHead, TableHeader, TableRow } from "@/components/ui/table";

const Home = async () => {
  const games = await steamAnalyserAPI.get<MostPlayedResponse[]>(Routes.MOST_PLAYED);
  console.log(games)
  return (
    <div>
      <Table>
        <TableCaption>Most Played Games</TableCaption>
        <TableHeader>
          <TableRow>
            <TableHead>Game</TableHead>
            <TableHead>Online Players</TableHead>
            <TableHead>Players Peak</TableHead>
          </TableRow>
        </TableHeader>
        <TableBody>
          {games.data.map(game => (
            <TableRow>
              <TableCell>
                <section>
                  <img src={game.capsuleImage}/>
                  {game.name}
                </section>
                </TableCell>
              <TableCell className="text-green-500 font-bold">{game.playersOnline}</TableCell>
              <TableCell>{game.peakInGame}</TableCell>
            </TableRow>
          ))}
        </TableBody>
      </Table>
    </div>
  );
}

export default Home;
