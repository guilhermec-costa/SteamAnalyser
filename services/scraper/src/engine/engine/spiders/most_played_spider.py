from typing import Any, Iterable
from scrapy.http import Response
import scrapy

class MostPlayedSpider(scrapy.Spider):
  name = "most_played";
  handle_httpstatus_list = [403]

  def start_requests(self) -> Iterable[scrapy.Request]:
    headers = { "user-agent": "Googlebot" };

    urls = [
      "https://steamdb.info/",
    ];

    for url in urls:
      yield scrapy.Request(url=url, callback=self.parse, method="GET", headers=headers);
  
  def parse(self, response: Response, **kwargs: Any) -> Any:
    print("url: " + response.url);

