import { Inter, Roboto } from "next/font/google";

const roboto = Roboto({
  weight: "400",
  subsets: ["latin"]
});

const inter = Inter({
  weight: "400",
  subsets: ["latin"]
});

export {
  roboto,
  inter
};