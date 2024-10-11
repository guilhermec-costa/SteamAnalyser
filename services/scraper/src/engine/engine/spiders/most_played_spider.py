from typing import Any, Iterable
from scrapy.http import Response
import scrapy

class MostPlayedSpider(scrapy.Spider):
  name = "most_played";

  def start_requests(self) -> Iterable[scrapy.Request]:
    headers = {
      "user-agent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/129.0.0.0 Safari/537.36",
      "Referer": "https://steamdb.info/"
    };

    urls = [
      "https://steamcharts.com",
    ];

    for url in urls:
      yield scrapy.Request(url=url, callback=self.parse, method="GET", headers=headers);
  
  def parse(self, response: Response, **kwargs: Any) -> Any:
    print("url: " + response.body);

