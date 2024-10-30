export interface MostPlayedResponse {
  content: {
    _24hpeak: number,
    current_players: number,
    name: string,
    appImage: string
  }[]
}