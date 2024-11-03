"use client"
import { MostPlayedItem } from "@/app/types/mostPlayedItem";
import { ColumnDef } from "@tanstack/react-table";

export const columns: ColumnDef<MostPlayedItem>[] = [
  {
    accessorKey: "appImage",
    header: "Image",
    cell: ({row}) => {
      console.log(row)
      return <img src={row.getValue("appImage")} alt="app-image" className="h-16" />
    }
  },
  {
    accessorKey: "name",
    header: "Name"
  },
  {
    accessorKey: "current_players",
    header: "Players Online"
  },
  {
    accessorKey: "_24hpeak",
    header: "24h peak"
  }
]