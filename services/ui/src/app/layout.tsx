import type { Metadata } from "next";
import { roboto, inter } from "@/styles/fonts";
import "./globals.css";


export const metadata: Metadata = {
  title: "Steam Analyser",
  description: "SteamAnalyser API frontend",
};

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode;
}>) {
  return (
    <html lang="en">
      <body className={`antialiased dark ${roboto.className}`}>
        {children}
      </body>
    </html>
  );
}
