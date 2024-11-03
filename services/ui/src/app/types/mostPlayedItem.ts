export interface MostPlayedItemWrapper {
  content: MostPlayedItem[]
}

export interface MostPlayedItem {
  _24hpeak: number,
  current_players: number,
  name: string,
  appImage: string
}