from typing import Union
from fastapi import FastAPI
from src.constants.routes import routes;

class Application:

  def __init__(self) -> None:
    self.app = FastAPI();
    self.init_routes();

  def init_routes(self) -> None:
    @self.app.get(routes.get("most_played"))
    def dashboard_root():
      return "hello world";

  def get_app_instance(self):
    return self.app;

app = Application().get_app_instance();